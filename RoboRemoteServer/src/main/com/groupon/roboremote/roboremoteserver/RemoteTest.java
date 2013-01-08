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

package com.groupon.roboremote.roboremoteserver;

import android.os.Handler;
import android.test.ActivityInstrumentationTestCase2;
import android.app.Activity;
import android.view.View;
import com.groupon.roboremote.roboremoteserver.robotium.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import com.groupon.roboremote.roboremoteserver.httpd.NanoHTTPD;

public abstract class RemoteTest<T extends Activity> extends ActivityInstrumentationTestCase2 {
    protected Solo2 solo;

    public RemoteTest(Class<T> activityClass) throws ClassNotFoundException {
        super("blah", activityClass);
    }

    public void startServer() throws Exception {
        System.out.println("- Starting HTTP service");
        try
        {
            new RCHttpd(solo);
        }
        catch( IOException ioe )
        {
            System.out.println("Couldn't start server:\n" + ioe);
            System.exit( -1 );
        }
        System.out.println("Listening on port 8080. Kill test to stop.\n");
        while(true) {
            Thread.sleep(5000);
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Initialize Robotium Solo singleton
        solo = new Solo2(getInstrumentation(), getActivity());
        SoloSingleton.set(solo);
    }

    public class RCHttpd extends NanoHTTPD {
        Solo2 solo = null;
        public RCHttpd(Solo2 _solo) throws IOException {
            super(8080, new File("/"));
            solo = _solo;
        }

        private String[] getTypeEquivalents(String type) {
            String[] returnArray = null;

            for (int x = 0; x < R.array.class.getFields().length; x++) {
                try {

                    ArrayList<String> tmpArray = new ArrayList<String>();

                    Collections.addAll(tmpArray, getInstrumentation().getContext().getResources().getStringArray(R.array.class.getFields()[x].getInt(null)));

                    if (tmpArray.size() == 0)
                        continue;

                    // safety precaution.. make sure the first item is "TYPE"
                    if (tmpArray.get(0).compareTo(Constants.TYPE_EQUIVALENT_IDENTIFIER) != 0)
                        continue;

                    tmpArray.remove(0);

                    // look through the rest of the array and see if one of the types matches the type we passed in
                    for (String tmpString: tmpArray) {
                        if (tmpString.compareTo(type) == 0) {
                            returnArray = tmpArray.toArray(new String[tmpArray.size()]);
                        }
                    }

                    if (returnArray != null)
                        break;

                } catch (Exception e) {
                    // we'll get here if we ran out of types to look at
                    break;
                }
            }

            return returnArray;
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

        private JSONObject processOperations(JSONArray operations) throws Exception {
            JSONArray returnValues = new JSONArray();
            JSONObject returnObject = new JSONObject();

            // the idea here is that there may be multiple operations
            // each subsequent operation is called on the return value of the first operation
            Object currentClassObject = null;
            for (int x = 0; x < operations.length(); x++) {
                JSONObject operation = operations.getJSONObject(x);

                // see if this has a query.. normally only the 1st op will
                String query = null;
                if (operation.has(Constants.REQUEST_QUERY)) {
                    query = operation.getString(Constants.REQUEST_QUERY);
                }

                // need to find a class object to work on if one isn't already defined
                if (currentClassObject == null) {
                    // let's find a class based on the query
                    // special case for solo
                    if (query.equals(Constants.ROBOTIUM_SOLO)) {
                        currentClassObject = solo;
                    } else {
                        Class c = Class.forName(query);
                        try {
                            // try instantiating.. if that doesn't work then it is probably a static class
                            currentClassObject = c.newInstance();
                        } catch (Exception e) {
                            currentClassObject = c;
                        }
                    }
                }

                // the op type could be an Constants.REQUEST_OPERATION or a Constants.REQUEST_FIELD
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
                }
            }

            return returnObject;
        }
        
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

        private ArbitraryItemStruct runArbitraryMethod(Object classObject, String methodName, Object[] args) throws Exception {
            ArbitraryItemStruct methodResults = new ArbitraryItemStruct();

            // generate list of arg class types
            Class[] argTypes = new Class[args.length];

            // declare a temp array for a copy of the args array incase we need type replacement
            Object[] newArgs = new Object[args.length];
            
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
                // Boolean to track whether or not we converted any arguments for the method match being evaluated
                Boolean convertedArgumentsForCurrentMethod = false;
                
                // try to match up the name, # args and method signature
                if (method.getName().equals(methodName) && method.getParameterTypes().length == argTypes.length) {
                    // replicate the args array
                    int x = 0;
                    for (Object arg: args) {
                        argTypes[x] = arg.getClass();
                        newArgs[x] = args[x];
                        x++;
                    }

                    // go through each param type and try to match things up
                    Class<?>[] paramTypes = method.getParameterTypes();
                    x = 0;
                    int matches = 0;
                    for (Class<?> paramClass: paramTypes) {
                        // now get the known types array that matches the argTypes type
                        String currentClsArg = argTypes[x].toString();
                        x++;

                        // get just the class name
                        currentClsArg = currentClsArg.substring(currentClsArg.lastIndexOf(".") + 1);

                        // it's possible that null was passed in.. if so we'll automatically say that it matches but was converted
                        if (currentClsArg.startsWith("JSONObject") && newArgs[x - 1].toString().compareTo("null") == 0) {
                            newArgs[x - 1] = null;
                            convertedArgumentsForCurrentMethod = true;
                            matches++;
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
                                matches++;
                                break;
                            }

                            // special case for String to Class or String to View conversion
                            // some functions want a class and we'll treat String and Class as equivalent if the string exists as a class
                            if ((paramClassStr.contains(Constants.ARGUMENT_TYPE_CLASS) 
                                    || paramClassStr.contains(Constants.ARGUMENT_TYPE_VIEW) 
                                    || paramClassStr.contains(Constants.ARGUMENT_TYPE_WIDGET)) && knowType.equals(Constants.ARGUMENT_TYPE_STRING)) {
                                // need to check for this item
                                try {
                                    Class findC = Class.forName((String)args[matches]);
                                    newArgs[matches] = findC;
                                    convertedArgumentsForCurrentMethod = true;
                                    matches++;
                                    break;
                                } catch (Exception e) {

                                }

                                // if that didn't work.. let's see if it matches a known view
                                for (View view: solo.getCurrentViews()) {
                                    if (view.toString().contains((String)args[matches])) {
                                        newArgs[matches] = view;
                                        convertedArgumentsForCurrentMethod = true;
                                        matches++;
                                        break;
                                    }
                                }
                            }

                        }
                    }

                    if (matches == paramTypes.length) {
                        // the idea here is to find the best match
                        // the ideal match is one where we didn't do any argument conversion
                        // TODO: this still needs to be smarter
                        
                        // for now do this if:
                        // 1. We don't have a match already(m == null)
                        // 2. We have a match, but the newer match didn't require argument conversion
                        if (m == null ||
                           (m != null && !convertedArgumentsForCurrentMethod)
                           ) {
                            // replace args with the temp args array incase we converted any arguments
                            argsToPass = newArgs;
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
                e.getMessage();
            }

            return returnVal;
        }

        private String processGet(String uri, Properties params) {
            String msg = "";
            Enumeration eparams = params.propertyNames();
            while ( eparams.hasMoreElements())
            {
                String value = (String)eparams.nextElement();
                msg = msg + "  PRM: '" + value + "' = '" +
                        params.getProperty( value ) + "'\n" ;
            }

            return msg;
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
