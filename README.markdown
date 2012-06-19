# RoboRemote - A remote control framework for Robotium

## Overview
***
**RoboRemote** is a remote control framework for Robotium.  The goal of RoboRemote is to allow for more complex test scenarios by letting the automator write their tests using standard desktop Java/JUnit(other frameworks to be supported in the future).  All of the Robotium Solo commands are available.  RoboRemote also provides some convencience classes to assist in common tasks such as interacting with list views.

RoboRemote is loosely modeled after Frank for iOS.

### Currently Supported Android Versions

* 2.3+

## Architecture
***
RoboRemote consists of two components - RoboRemoteServer and RoboRemoteClient.  The entire project is managed by Maven and both components are provided as artifacts.

### RoboRemoteServer
RoboRemoteServer acts as a HTTP interface to Robotium. It accepts requests and provides responses in JSON format.  The input JSON describes the class, method and method parameters for the function that is being called.

**Example request**

*Traditional Robotium Syntax*: <pre><code>View tView = solo.getView(android.widget.EditText.class, 0);</code></pre>
*RoboRemote map request*(POST to http://&lt;device ip&gt;:8080/map):
<pre><code>request={
    "operations": [{
        "query": "solo",
        "operation": {
            "method_name": "getView",
            "arguments": [ "android.widget.EditText", 0 ]
        }
}]}</code></pre>
*RoboRemote map response*
<pre><code>{
    "results": [
        "android.widget.EditText@470fcf18"
    ],
    "outcome": "SUCCESS"
}
</code></pre>

**How did that work?**

RoboRemote uses Java reflection along with some additional logic to turn the JSON based requests into function calls.  All return values from functions are returned as a JSONArray of values.  If a function results in a List or an Array then the JSONArray will contain all of the data from the functions return value.

RoboRemoteServer contains a resource file(&lt;root&gt;/RoboRemoteServer/res/values/types.xml) that maps parameter types to equivalent parameter types.  This is used to determine if the value that was passed in through JSON(ex: Integer) can be used as an argument to a function that takes a different numerical type.  If you are trying to call a function and it is not working properly then a missing mapping in this file is the likely culprit.

**What do I do with the String version of the View I requested?**

The textual versions of views or class names can be passed back is an argument to a function that requires a class or a view.  An example will be shown in the RoboRemoteClient section of this README.

### RoboRemoteClient
RoboRemoteClient is a java library(jar) that can be utilized in a JUnit 4.11 test project.  It provides the following functionality:

* Device/Emulator functions(start app, close app, clear app data)
* Test logging(using SLF4J and logback)
* Logcat collection
* Event based testing(currently logcat monitoring)
* Failure screenshots
* Robotium Solo emulation
* Client class to call almost any class/function you want
* Convenience classes for several components(ex: click an item at an absolute listview item index)

#### Robotium Solo Emulation

RoboRemoteClient provides com.groupon.roboremote.RoboRemoteClient.Solo which maps all Solo commands that were available as of Robotium 3.1.

**Example Robotium Solo request**

*Traditional Robotium Syntax*: 
<pre><code>View tView = solo.getView(android.widget.EditText.class, 0);
solo.enterText(tView, "Text to enter");</code></pre>
*RoboRemote client request*(using com.groupon.roboremote.RoboRemoteClient.Solo):
<pre><code>String tView = Solo.getView("android.widget.EditText", 0);
Solo.enterText(tView, "Text to enter");
</code></pre>

#### Arbitrary Function Calls

RoboRemoteClient also provides a method(in com.groupon.roboremote.RoboRemoteClient.Client) to call any function in a static/non-static class.  The map method is defined as map(String className, String method_name, Object … parameters).  If the call is successful then it returns a JSONArray of the results.  If the method only has a single return value then it will be in the first element of the JSONArray.  Elements in lists/arrays are returned in the same position in the JSONArray as they would be for the normal function call.

**Example function call**

*Traditional method call in Robotium test*:
<pre><code>java.lang.System.exit(0);</code></pre>

*RoboRemote client request*:
<pre><code>Client.map("java.lang.System", "exit", 0)</code></pre>

***Note about function calls***: You can call methods in static/non-static classes, but cannot call methods in already instantiated classes.  In order to use already instantiated classes you will have to add hook methods in your test runner(explained in the Getting Started section of this README).  The only exception to this is the Robotium Solo class.  This class is pre-instantiated and can be referenced as "solo" in a map call.

##### Fields

You may have a neeed to retrieve content from fields in a class.  This can be done using the mapField(String className, String) function instead of the map(…) function.

**Example field accessor**

*Traditional field access in Robotium test*:
<pre><code>solo.DELETE</code></pre>

*RoboRemote client request*:
<pre><code>int DELETE = Client.mapField("solo", "DELETE", 0).getInt(0)</code></pre>

#### Method Chaining

RoboRemoteClient also allows you to call functions on the return value of a previous function via the com.groupon.roboremote.RoboRemoteClient.QueryBuilder class.  You can chain together as many method calls as you would like.  The drawback to this class is that you have to map Solo calls instead of using the pre-defined Solo class.

*Traditional method call*:
<pre><code>float textSize = solo.getText("Sample Text").getTextSize();</code></pre>

*RoboRemote QueryBuilder method call*:
<pre><code>QueryBuilder query = new QueryBuilder();
query.map("solo", "getText", "Sample Text").call("getTextSize");
JSONArray result = query.execute();
float textSize = new Double(result.getDouble(0)).floatValue();</code></pre>

*Traditional method call with a Field*:
<pre><code>System.out.println("Foo bar");</code></pre>

*RoboRemote QueryBuilder call*:
<pre><code>QueryBuilder query = new QueryBuilder();
query.mapField("java.lang.System", "out".call("println", "Foo bar").execute();</code></pre>


## Getting Started
***
You need two things to get started with RoboRemote.

* A test runner.  This is very similar to a Robotium test class except that it extends com.groupon.roboremote.roboremoteserver.RemoteTest instead of ActivityInstrumentationTestCase2.  You can actually take the example below and simply change the namespace and target test class.  The provided examples(described below) also contain Maven pom files to show how to compile this.

	<pre><code>package com.groupon.roboremote.example.helloworldtestrunner;
import com.groupon.roboremote.example.helloworld.HelloWorld;
import com.groupon.roboremote.roboremoteserver.*;
public class Runner extends RemoteTest<HelloWorld> {
    public Runner() throws ClassNotFoundException {
        super(HelloWorld.class);
    }
    public void testRCRunner() throws Exception {
        startServer();
    }
}</code></pre>

* Tests!  These are JUnit 4.10 test classes that extend com.groupon.roboremote.roboremoteclient.TestBase.  Example tests are provided in the source repository.  These can also be compiled with Maven and a sample pom is provided with the example.  
The test executor requires a few environment variables to be set:
	1. ROBO_APP_PACKAGE - This is the package name of the application under test(ex: com.groupon.roboremote.example.HelloWorld)
	2. ROBO_TEST_CLASS - This is the class name that contains the test method we are usng(ex: com.groupon.roboremote.example.helloworldtestrunner.Runner)
	3. ROBO_TEST_RUNNER - The instrumentation test runner to be used(ex: com.groupon.roboremote.example.helloworldtestrunner/android.test.InstrumentationTestRunner)

	These can be alternatively defined if your tests have a @BeforeClass method that overrides the setUpApp() method from TestBase
	<pre><code>@BeforeClass
    public static void setUpApp() {
        Device.setAppEnvironmentVariables("com.groupon.roboremote.example.helloworld", "com.groupon.roboremote.example.helloworldtestrunner.Runner", "com.groupon.roboremote.example.helloworldtestrunner/android.test.InstrumentationTestRunner");
    }</code></pre>

Once you have these two items along with your app then you can install the app under test and the test runner to your device and then begin executing tests against an attached device/emulator using desktop JUnit either from maven or the IDE of your choice.

### Maven Depenendencies
#### Test Runner
These are the maven dependencies you need to declare in your test runner pom:

<pre><code><syntaxhighlight lang="xml"><dependency>
            <groupId>com.groupon.roboremote</groupId>
            <artifactId>roboremoteserver</artifactId>
            <version>0.2</version>
            <type>apklib</type>
        </dependency></syntaxhighlight></pre></code>

#### Tests
These are the maven dependencies you need to declare in your tests pom:

<pre><code><syntaxhighlight lang="xml"><dependency>
            <groupId>com.groupon.roboremote</groupId>
            <artifactId>roboremoteclient</artifactId>
            <version>0.2</version>
        </dependency>
        <dependency>
            <groupId>com.groupon.roboremote.roboremoteclient</groupId>
            <artifactId>junit</artifactId>
            <version>0.2</version>
        </dependency></syntaxhighlight></pre></code>

## Test Hooks
You may find that you want to do more complicated function calls that make more sense to do in Android code rather than through function mapping.  Fortunately this is very easy since RoboRemote can call any arbitrary function.  The easiest way to do this is to add an additional class(Ex: TestHook) along side your Runner class.

*Example testhook class*:
<pre><code>package com.groupon.roboremote.example.helloworldtestrunner;
public class TestHook {
    public static void complicatedFunctionCalls() {
        // Do some stuff here
    }
}</code></pre>

*Invocation of testhook function*:
<pre><code>Client.map("com.groupon.roboremote.example.helloworldtestrunner.TestHook", "complicatedFunctionCalls")</code></pre>

## Examples
***
An example project is provided in the examples/HelloWorld directory.  The directory structure is as follows:

* helloworld - This is the application under test which provides a simple list view and an activity to launch based on pressing an itme in the list view
* helloworldtestrunner - This is the test runner which starts up the RoboRemote HTTP listener and the application under test.
* Tests - This contains the desktop JUnit based example tests for the HelloWorld project.  Under Tests/src/test is a SampleTests.java which contains a few tests demonstrating various RoboRemote functions.

### Compiling the examples

Running "mvn clean install" from the "examples" directory will build the examples.  Be sure to run "mvn clean install" from the root first to compile the latest library versions

### Installing helloworld/helloworldtestrunner

Execute the following from your repository root:

1. adb install examples/HelloWorld/helloworld/target/HelloWorld-1.0.apk
2. adb install examples/HelloWorld/helloworldtestrunner/target/helloworldtestrunner-1.apk

### Executing the examples

Execute the following from examples/HelloWorld/Tests

1. mvn test -Dtest=SampleTests

## TODO/Limitations
***
1. Add compatibility for Android 2.1 - 2.2

## Links
***
**Robotium** - <http://code.google.com/p/robotium/>

**JUnit** - <http://www.junit.org/>

**Frank** - <https://github.com/moredip/Frank>


