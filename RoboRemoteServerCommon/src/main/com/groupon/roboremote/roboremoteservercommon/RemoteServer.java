/*
 * Copyright (c) 2012, Groupon, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of GROUPON nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.</div>
 */

package com.groupon.roboremote.roboremoteservercommon;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import android.view.View;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class RemoteServer {
    // get an instantiated class based on predefined keys(ex: solo for Robotium)
    protected abstract Object getInstantiatedClass(String query);

    // Get a view with the specified name(may not be supported by all automation platforms)
    protected abstract View getView(String viewName);

    public void startServer(int port) throws Exception {
        System.out.println("startServer:: Starting HTTP service");
        try
        {
            new RCHttpd(port);
        }
        catch( IOException ioe )
        {
            System.out.println("startServer:: Couldn't start server:\n" + ioe);
            System.exit( -1 );
        }
        System.out.println("startServer:: Listening on port " + port + ". Kill test to stop.\n");
        while(true) {
            Thread.sleep(5000);
        }
    }

    public class RCHttpd extends NanoHTTPD {
        private Object lastResponseObject = null;
        private HashMap<String, Object> storedResponses = new HashMap<String, Object>();

        public RCHttpd(int port) throws IOException {
            super(port, new File("/"));
        }

        public Response serve( String uri, String method, Properties header, Properties parms, Properties files ) {
            String msg = "";

            uri = uri.substring(1);

            if ( method.equalsIgnoreCase( Constants.NANO_POST )) {
                msg = processPost(uri, parms).toString();
            } else if ( method.equalsIgnoreCase( Constants.NANO_GET )) {
                msg = processGet(uri, parms);
            } else {
                // dunno what to do
            }

            return new Response( HTTP_OK, MIME_HTML, msg );
        }


        /**
         * Process the list of passed in operations and return the result
         * @param operations
         * @return
         * @throws Exception
         */
        private JSONObject processOperations(JSONArray operations) throws Exception {
            JSONArray returnValues = new JSONArray();
            JSONObject returnObject = new JSONObject();

            // the idea here is that there may be multiple operations
            // each subsequent operation is called on the return value of the first operation
            Object currentClassObject = null;
            for (int x = 0; x < operations.length(); x++) {
                JSONObject operation = operations.getJSONObject(x);
                System.out.println("processOperations:: Current operation: " + operation);

                Object[] classArgs = new Object[0];

                // see if this has a query.. normally only the 1st op will
                String query = null;
                if (operation.has(Constants.REQUEST_QUERY)) {
                    query = operation.getString(Constants.REQUEST_QUERY);
                } else if(operation.has(Constants.REQUEST_INSTANTIATE)) {
                    query = operation.getString(Constants.REQUEST_INSTANTIATE);

                    // get arguments for class instantiation
                    JSONArray args = new JSONArray();
                    if (operation.has(Constants.REQUEST_ARGUMENTS)) {
                        args = operation.getJSONArray(Constants.REQUEST_ARGUMENTS);
                        classArgs = new Object[args.length()];
                        for (int xx = 0; xx < args.length(); xx++) {
                            classArgs[xx] = args.get(xx);
                        }
                    }
                }

                // restore stored item if this was a stored value
                if (query != null && query.startsWith(Constants.STORED)) {
                    currentClassObject = storedResponses.get(query);
                }

                // need to find a class object to work on if one isn't already defined
                if (currentClassObject == null && query != null) {
                    // let's find a class based on the query
                    // This function is abstract and delegated to the implementing server
                    Object delegatedClassObject = getInstantiatedClass(query);

                    if (delegatedClassObject != null) {
                        currentClassObject = delegatedClassObject;
                    } else {
                        Class c = Class.forName(query);
                        try {
                            // try instantiating.. if that doesn't work then it is probably a static class
                            currentClassObject = instantiateClass(c, classArgs);
                        } catch (Exception e) {

                        }

                        // if we still don't have one then assume it is static and assign to the found class
                        if (currentClassObject == null)
                            currentClassObject = c;
                    }
                }

                System.out.println("processOperations:: Working class: " + currentClassObject);

                // the op type could be an Constants.REQUEST_OPERATION or a Constants.REQUEST_FIELD or REQUEST_STORE or REQUEST_REMOVE
                // operations are method calls
                // fields are field accessors(returns the value of the field)
                if (operation.has(Constants.REQUEST_OPERATION)) {
                    // now run an arbitrary method based on the class
                    JSONObject op = operation.getJSONObject(Constants.REQUEST_OPERATION);

                    // get method name
                    String method = op.getString(Constants.REQUEST_METHOD_NAME);

                    // Get arguments and convert into an array of Objects
                    JSONArray args = new JSONArray();
                    if (op.has(Constants.REQUEST_ARGUMENTS)) {
                        args = op.getJSONArray(Constants.REQUEST_ARGUMENTS);
                    }

                    Object[] mArgs = new Object[args.length()];
                    for (int xx = 0; xx < args.length(); xx++) {
                        mArgs[xx] = args.get(xx);
                    }

                    ArbitraryItemStruct funcReturn = null;
                    Boolean callFailed = false;

                    try {
                        funcReturn = runArbitraryMethod(currentClassObject, method, mArgs);
                    } catch (Exception e) {
                        // this means something went wrong trying to call the function
                        String msg = e.getMessage();
                        e.printStackTrace();
                        returnObject.put(Constants.RESULT_OUTCOME, Constants.RESULT_FAILED);
                        returnObject.put(Constants.RESULT_REASON, msg);

                        callFailed = true;
                        break;
                    }

                    if (! callFailed) {
                        if (funcReturn.getReturnVal() != null) {
                            currentClassObject = funcReturn.getReturnVal();
                        }

                        returnValues = getReturnValues(funcReturn);
                        returnObject.put(Constants.RESULT_RESULTS, returnValues);
                        returnObject.put(Constants.RESULT_OUTCOME, Constants.RESULT_SUCCESS);
                    }

                    returnObject.put(Constants.RESULT_RESULTS, returnValues);
                } else if (operation.has(Constants.REQUEST_INSTANTIATE)) {
                    // Instantiate a class
                    JSONArray resultArray = new JSONArray();
                    resultArray.put(currentClassObject);
                    returnObject.put(Constants.RESULT_RESULTS, resultArray);
                    returnObject.put(Constants.RESULT_OUTCOME, Constants.RESULT_SUCCESS);
                } else if (operation.has(Constants.REQUEST_FIELD)) {
                    // we actually want a field accessor
                    String fieldName = operation.getString(Constants.REQUEST_FIELD);

                    ArbitraryItemStruct funcReturn = null;
                    Boolean callFailed = false;

                    try {
                        funcReturn = getArbitraryField(currentClassObject, fieldName);
                    } catch (Exception e) {
                        // this means something went wrong trying to call the function
                        String msg = e.getMessage();
                        returnObject.put(Constants.RESULT_OUTCOME, Constants.RESULT_FAILED);
                        returnObject.put(Constants.RESULT_REASON, msg);

                        callFailed = true;
                        break;
                    }

                    if (funcReturn.getReturnVal() != null) {
                        currentClassObject = funcReturn.getReturnVal();
                    }

                    returnValues = getReturnValues(funcReturn);
                    returnObject.put(Constants.RESULT_RESULTS, returnValues);
                    returnObject.put(Constants.RESULT_OUTCOME, Constants.RESULT_SUCCESS);
                } else if (operation.has(Constants.REQUEST_STORE)) {
                    // store the lastResponseObject
                    storedResponses.put(Constants.STORED + operation.getString(Constants.REQUEST_STORE), lastResponseObject);
                } else if (operation.has(Constants.REQUEST_REMOVE)) {
                    // remove the specified stored response
                    storedResponses.remove(operation.getString(Constants.REQUEST_STORE));
                } else if (operation.has(Constants.REQUEST_RETRIEVE)) {
                    // retrieve a stored response
                    currentClassObject = storedResponses.get(Constants.STORED + operation.get(Constants.REQUEST_RETRIEVE));
                }

                // store currentClassObject
                lastResponseObject = currentClassObject;
            }

            return returnObject;
        }

        /**
         * Returns a JSONArray representing the return values of the call
         * @param returnItem
         * @return
         */
        private JSONArray getReturnValues(ArbitraryItemStruct returnItem) {
            JSONArray returnValues = new JSONArray();

            String returnType = returnItem.getReturnType();

            if (returnType.contains(Constants.RETURN_TYPE_LIST) && returnType.contains(Constants.RETURN_TYPE_JAVA_UTIL)) {
                List<Object> funcList = (List<Object>) returnItem.getReturnVal();

                for (Object obj : funcList) {
                    returnValues.put(obj);
                }
            } else if (returnType.toLowerCase().contains(Constants.RETURN_TYPE_BOOLEAN)) {
                Boolean ret = (Boolean)returnItem.getReturnVal();
                returnValues.put(ret);
            } else if (returnType.toLowerCase().contains(Constants.RETURN_TYPE_ARRAY)) {
                // we need to iterate over this until we get an exception
                int x = 0;
                while(true) {
                    try {
                        returnValues.put(java.lang.reflect.Array.get(returnItem.getReturnVal(), x));
                    } catch (Exception e) {
                        break;
                    }
                    x++;
                }

                if (x == 0) {
                    // we didn't put anything in returnValues
                    // we'll just return true
                    returnValues.put(true);
                }
            } else if (! returnType.toLowerCase().contains(Constants.RETURN_TYPE_VOID)) {
                // we'll grab the result as an object and do what we can
                Object tmpObj = returnItem.getReturnVal();
                try {
                    returnValues.put(tmpObj);
                } catch(Exception ee) {
                    returnValues.put(true);
                }
            } else  {
                // this was a void method so there is no return value
            }

            return returnValues;
        }

        private ArbitraryItemStruct getArbitraryField(Object classObject, String fieldName) throws Exception {
            ArbitraryItemStruct fieldResults = new ArbitraryItemStruct();

            Field f = null;
            Object val = null;

            try {
                // if this was not an instantiated class then we actually want to get fields from the base object
                if (classObject.getClass().toString().contains(Constants.CLASS_TYPE_STATIC)) {
                    f = ((Class)classObject).getField(fieldName);
                    val = f.get((Class)classObject);
                } else {
                    f = classObject.getClass().getField(fieldName);
                    val = f.get(classObject);
                }
            } catch (NoSuchFieldException nsfe) {
                throw new Exception("Could not find field");
            }

            String returnType = f.getType().toString();
            fieldResults.setReturnType(returnType);

            fieldResults.setReturnVal(val);
            fieldResults.setField(f);

            return fieldResults;
        }

        /**
         * Private inner class to represent a complex return value from matchAndConvertArguments
         */
        private class MatchAndConvert {
            Boolean convertedArguments = false;
            Object[] arguments = null;
            int matches = 0;
            Boolean matchSucceeded = false;
        }

        /**
         * This method takes an array of Classes that represent parameter types for a method or class instantation
         * And an array of arguments to match up with that constructor/method signature
         * Some arguments may be converted during the process
         * @param paramTypesToMatch
         * @param args
         * @throws Exception
         */
        private MatchAndConvert matchAndConvertArguments(Class<?>[] paramTypesToMatch, Object[] args) throws Exception {
            // return value
            MatchAndConvert matchReturn = new MatchAndConvert();

            // array to contain the original argument types from "args"
            Class[] argTypes = new Class[args.length];
            // array to contain a copy of the "args" array which may be edited
            matchReturn.arguments = new Object[args.length];

            // replicate the args array
            int x = 0;
            for (Object arg: args) {
                argTypes[x] = arg.getClass();
                matchReturn.arguments[x] = args[x];
                x++;
            }

            // go through each param type and try to match things up
            x = 0;
            matchReturn.matches = 0;
            for (Class<?> paramClass: paramTypesToMatch) {
                // now get the known types array that matches the argTypes type
                String currentClsArg = argTypes[x].toString();
                x++;

                // see if this is a stored value
                if ((String.valueOf(args[matchReturn.matches])).startsWith(Constants.STORED)) {
                    // IF there is a stored value and it's type matches the type we are trying to match
                    if (storedResponses.containsKey((String)args[matchReturn.matches]) &&
                            storedResponses.get((String)args[matchReturn.matches]).getClass().toString().startsWith(paramClass.toString())) {
                        matchReturn.arguments[matchReturn.matches] = storedResponses.get((String)args[matchReturn.matches]);
                        matchReturn.matches++;
                        continue;
                    }
                }

                // get just the class name
                currentClsArg = currentClsArg.substring(currentClsArg.lastIndexOf('.') + 1);

                // it's possible that null was passed in.. if so we'll automatically say that it matches but was converted
                if (currentClsArg.startsWith("JSONObject") && matchReturn.arguments[x - 1].toString().compareTo("null") == 0) {
                    matchReturn.arguments[x - 1] = null;
                    matchReturn.convertedArguments = true;
                    matchReturn.matches++;
                    continue;
                }

                // get type equivalents
                // ex: int == Integer
                String[] knownTypes = getTypeEquivalents(currentClsArg);

                // if the knownTypes array is empty.. then we push the current type on for the type comparison
                // we have no better knowledge, but this allows comparison for types with no equivalents
                if (knownTypes == null || knownTypes.length == 0) {
                    knownTypes = new String[1];
                    knownTypes[0] = currentClsArg;
                }

                // see if one of the knownTypes matches the current paramClass
                String paramClassStr = paramClass.toString().substring(paramClass.toString().lastIndexOf(" ") + 1);
                String paramClassStrEnd = paramClass.toString().substring(paramClass.toString().lastIndexOf(".") + 1);
                for (String knowType: knownTypes) {
                    if (knowType.compareTo(paramClassStrEnd) == 0) {
                        matchReturn.matches++;
                        break;
                    }

                    // special case for String to Class or String to View conversion
                    // some functions want a class and we'll treat String and Class as equivalent if the string exists as a class
                    if ((paramClassStr.contains(Constants.ARGUMENT_TYPE_CLASS)
                            || paramClassStr.contains(Constants.ARGUMENT_TYPE_VIEW)
                            || paramClassStr.contains(Constants.ARGUMENT_TYPE_WIDGET)) && knowType.equals(Constants.ARGUMENT_TYPE_STRING)) {

                        // see if there is a class that represents this value
                        try {
                            Class findC = Class.forName((String)args[matchReturn.matches]);
                            matchReturn.arguments[matchReturn.matches] = findC;
                            matchReturn.convertedArguments = true;
                            matchReturn.matches++;
                            break;
                        } catch (Exception e) {

                        }

                        // try to find a view instead
                        // not all frameworks will support this and getView may return null in those cases
                        View viewFinder = getView((String)args[matchReturn.matches]);
                        if (viewFinder != null) {
                            matchReturn.arguments[matchReturn.matches] = viewFinder;
                            matchReturn.convertedArguments = true;
                            matchReturn.matches++;
                            break;
                        }
                    }

                }

                // last ditch effort.. this param type might match our previous process result
                // we also bail if the amount of matches already matches the amount of parameters we have evaluated
                if (lastResponseObject == null || matchReturn.matches == x)
                    continue;

                // If the last response type matches the current method param type then use it
                if (lastResponseObject.getClass().toString().startsWith(paramClass.toString())) {
                    matchReturn.arguments[matchReturn.matches] = lastResponseObject;
                    matchReturn.convertedArguments = true;
                    matchReturn.matches++;
                }
            }

            // set matchSucceeded if matches == # of parameters
            if (matchReturn.matches == paramTypesToMatch.length) {
                matchReturn.matchSucceeded = true;
            }

            return matchReturn;
        }

        /**
         * Instantiate a class based on a found class and list of arguments
         * @param c
         * @return
         */
        private Object instantiateClass(Class c, Object[] args) {
            Object instantiatedClass = null;
            Constructor constructorToInstantiate = null;
            Object[] argsToPass = new Object[args.length];
            try {
                // if there are no args just try the default constructor.. otherwise search for one
                if (args.length == 0) {
                    instantiatedClass = c.newInstance();
                } else {
                    for (Constructor constructor : c.getDeclaredConstructors()) {
                        if (constructor.getParameterTypes().length == args.length) {
                            MatchAndConvert matchedData = matchAndConvertArguments(constructor.getParameterTypes(), args);
                            if (constructorToInstantiate == null ||
                                    (constructorToInstantiate != null && !matchedData.convertedArguments)
                                    ) {
                                // replace args with the temp args array incase we converted any arguments
                                argsToPass = matchedData.arguments;
                                constructorToInstantiate = constructor;
                            }
                        }
                    }

                    if (constructorToInstantiate != null) {
                        instantiatedClass = constructorToInstantiate.newInstance(argsToPass);
                    }
                }
            } catch (Exception ee) {
                System.out.println("instantiateClass: " + ee.getMessage());
            }

            return instantiatedClass;
        }

        /**
         * Run a method on the current class object with the specified arguments list
         * @param classObject
         * @param methodName
         * @param args
         * @return
         * @throws Exception
         */
        private ArbitraryItemStruct runArbitraryMethod(Object classObject, String methodName, Object[] args) throws Exception {
            ArbitraryItemStruct methodResults = new ArbitraryItemStruct();

            // declare an array for the final arg list
            Object[] argsToPass = new Object[args.length];

            // find the method
            Method m = null;
            Method[] methods = classObject.getClass().getMethods();

            // if this was not an instantiated class then we actually want to get methods from the base object
            if (classObject.getClass().toString().contains(Constants.CLASS_TYPE_STATIC)) {
                methods = ((Class)classObject).getMethods();
            }

            // loop through all the methods and try to manually match the signature
            // based on the method name and argument type equivalents
            for (Method method: methods) {
                // try to match up the name, # args and method signature
                if (method.getName().equals(methodName) && method.getParameterTypes().length == args.length) {
                    MatchAndConvert matchedData = matchAndConvertArguments(method.getParameterTypes(), args);

                    if (matchedData.matches == method.getParameterTypes().length) {
                        // the idea here is to find the best match
                        // the ideal match is one where we didn't do any argument conversion
                        // TODO: this still needs to be smarter

                        // for now do this if:
                        // 1. We don't have a match already(m == null)
                        // 2. We have a match, but the newer match didn't require argument conversion
                        if (m == null ||
                                (m != null && !matchedData.convertedArguments)
                                ) {
                            // replace args with the temp args array incase we converted any arguments
                            argsToPass = matchedData.arguments;
                            m = method;
                        }
                    }
                }
            }

            // run the method if one was found
            // otherwise throw an exception
            if (m == null) {
                throw new Exception("Could not find method");
            } else {
                // check return type for the method
                String returnType = m.getReturnType().toString();
                methodResults.setReturnType(returnType);

                if (! returnType.toLowerCase().contains(Constants.RETURN_TYPE_VOID)) {
                    Object retData = m.invoke(classObject, argsToPass);
                    methodResults.setReturnVal(retData);
                } else {
                    m.invoke(classObject, argsToPass);
                }
            }


            return methodResults;
        }

        private JSONObject processPost(String uri, Properties params) {
            JSONObject returnVal = new JSONObject();
            try {
                if (uri.equalsIgnoreCase(Constants.REQUEST_MAP)) {
                    JSONObject request = new JSONObject(params.getProperty(Constants.REQUEST));

                    // see if the request has an "operations" array
                    JSONArray operations = null;
                    if (request.has(Constants.REQUEST_OPERATIONS)) {
                        operations = request.getJSONArray(Constants.REQUEST_OPERATIONS);
                    } else {
                        // let's push the request onto the operations array
                        operations = new JSONArray("[" + request.toString() + "]");
                        //operations.put(request);
                    }

                    returnVal = processOperations(operations);
                } else {
                    // not sure what to do yet
                }
            } catch (Exception e) {
                System.out.println("processPost:: POST failed: " + e.getMessage());
            }

            System.out.println("processPost:: Return value: " + returnVal);
            return returnVal;
        }

        private String processGet(String uri, Properties params) {
            String msg = "";
            JSONObject returnObject = new JSONObject();

            try {
                if (uri.equalsIgnoreCase(Constants.REQUEST_HEARTBEAT)) {
                    returnObject.put(Constants.RESULT_OUTCOME, Constants.RESULT_SUCCESS);
                } else {
                    returnObject.put(Constants.RESULT_OUTCOME, Constants.RESULT_FAILED);
                }
            } catch (Exception e) {
                e.getMessage();
            }

            return returnObject.toString();
        }

        /**
         * Returns an array of "equivalent" object types for a specified type
         * Ex: Integer, int, Long, long, Float, float are all considered to be the same for function matching
         * @param type
         * @return
         * @throws Exception
         */
        private String[] getTypeEquivalents(String type) throws Exception {
            // Build equivalence table
            String[] StringArray = {"String"};
            String[] IntegerArray = {"Integer", "int", "Long", "long", "Float", "float"};
            String[] BooleanArray = {"Boolean", "boolean"};
            HashMap<String, String[]> typeHash = new HashMap<String, String[]>();
            typeHash.put("String", StringArray);
            typeHash.put("Integer", IntegerArray);
            typeHash.put("Boolean", BooleanArray);

            for (String key : typeHash.keySet()) {
                String[] typeArray = typeHash.get(key);
                for (String entry : typeArray) {
                    if (entry.equals(type)) {
                        return typeArray;
                    }
                }
            }

            return null;
        }

        /**
         * Struct to hold return values from executing an operation
         */
        private class ArbitraryItemStruct
        {
            // would use appropriate names
            private Object _returnVal;
            private Object _field;
            private String _returnType;

            public ArbitraryItemStruct()
            {
                _returnVal    = null;
                _returnType = null;
                _field = null;
            }

            public void setReturnVal(Object returnVal) {
                _returnVal = returnVal;
            }

            public void setReturnType(String returnType) {
                _returnType = returnType;
            }

            public void setField(Object field) {
                _field = field;
            }

            public Object getReturnVal()
            {
                return (_returnVal);
            }

            public String getReturnType()
            {
                return (_returnType);
            }

            public Object getField()
            {
                return (_field);
            }
        }
    }
}
