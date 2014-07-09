# RoboRemote - A remote control framework for Robotium and UIAutomator

## Overview
***
**RoboRemote** is a remote control framework for Robotium/UIAutomator.  The goal of RoboRemote is to allow for more complex test scenarios by letting the automator write their tests using standard desktop Java/JUnit(other frameworks to be supported in the future).  All of the Robotium Solo commands and UIAutomator commands are available.  RoboRemote also provides some convencience classes to assist in common tasks such as interacting with list views.

RoboRemote is loosely modeled after Frank for iOS.

### Currently Supported Android Versions

* 2.3+ for Robotium
* 4.2.2+ for UIAutomator

### Specifying Device to Use
If you want to specify a device by serial # for either Robotium or UiAutomator then the ROBO_SERIAL_NUMBER environment variable should be set to the serial number of the device/emulator


## Requirements
***
### UiAutomator Remote
UiAutomator remote requires Android API Level 18 to be installed as a maven artifact.  It is suggested that the maven android sdk deployer is used for this(https://github.com/mosabua/maven-android-sdk-deployer).  Use the tool to install API 18(mvn install -P 4.3) libraries.  This will install the android and uiautomator library that is required.

*Note*: An API Level 17(4.2.2) system can be used to execute tests but calls to UiAutomator functions that require API 18 will fail.

## Getting Started - UiAutomator Remote

To use UiAutomator Remote you only need tests with a few setup steps as follows(See examples/HelloWorld/Tests/AutomatorTests.java for an example junit test setup):

1. Create a test class that extends com.groupon.roboremote.uiautomatorclient.TestBase
2. Provide the location of the UiAutomatorServer JAR.  There are two ways to do this:
   1. Set ROBO_UIAUTOMATOR_JAR as an environment variable to the location of the jar
   2. OR Call setAppEnvironmentVariables("location of jar")  in a @BeforeClass
3. In @Before call super.setUp("test name") and then any commands you need in order to start the app under test4. In @After call tearDown()

To get the JAR file either build it from source(in the UIAutomatorServer directory) or get it from maven(replace 0.6.0-b2 with the latest version #):
<pre><code>mvn org.apache.maven.plugins:maven-dependency-plugin:2.4:get -DrepoUrl=http://oss.sonatype.org/content/repositories/snapshots -Dartifact=com.groupon.roboremote:uiautomatorserver:0.6.0-b2 -Ddest=uiautomatorserver.jar</code></pre>

### Maven Depenendencies
#### Tests
These are the maven dependencies you need to declare in your tests pom:

<pre><code>&lt;dependency>
	&lt;groupId>com.groupon.roboremote&lt;/groupId>
    &lt;artifactId>uiautomatorclient&lt;/artifactId>
    &lt;version>0.6.0-b2&lt;/version>
&lt;/dependency></code></pre>

*NOTE*: You must add the sonatype snapshots repo to your repositories list.  Once 0.5.1 is final this will not be necessary.  Add the repository as follows:

<pre><code>&lt;repository>
    &lt;id>oss-sonatype&lt;/id>
    &lt;name>oss-sonatype&lt;/name>
    &lt;url>https://oss.sonatype.org/content/repositories/snapshots/&lt;/url>
    &lt;snapshots>
        &lt;enabled>true</enabled>
    &lt;/snapshots>
&lt;/repository></code></pre>


## Getting Started - Robotium Remote
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
	3. ROBO_TEST_RUNNER - The instrumentation test runner to be used(ex: com.groupon.roboremote.example.helloworldtestrunner/com.groupon.roboremote.roboremoteserver.RemoteTestRunner)

	These can be alternatively defined if your tests have a @BeforeClass method that overrides the setUpApp() method from TestBase
	<pre><code>@BeforeClass
    public static void setUpApp() {
        Device.setAppEnvironmentVariables("com.groupon.roboremote.example.helloworld", "com.groupon.roboremote.example.helloworldtestrunner.Runner", "com.groupon.roboremote.example.helloworldtestrunner/com.groupon.roboremote.roboremoteserver.RemoteTestRunner");
    }</code></pre>

Once you have these two items along with your app then you can install the app under test and the test runner to your device and then begin executing tests against an attached device/emulator using desktop JUnit either from maven or the IDE of your choice.

### Maven Dependencies
#### Test Runner
These are the maven dependencies you need to declare in your test runner pom:

<pre><code>&lt;dependency>
    &lt;groupId>com.groupon.roboremote&lt;/groupId>
    &lt;artifactId>roboremoteserver&lt;/artifactId>
    &lt;version>0.6.0-b2&lt;/version>
    &lt;type>apklib&lt;/type>
&lt;/dependency></code></pre>

If you are using Gradle an AAR is also provided:

<pre><code>&lt;dependency>
    &lt;groupId>com.groupon.roboremote&lt;/groupId>
    &lt;artifactId>roboremoteserveraar&lt;/artifactId>
    &lt;version>0.6.0-b2&lt;/version>
    &lt;type>aar&lt;/type>
&lt;/dependency></code></pre>

#### Tests
These are the maven dependencies you need to declare in your tests pom:

<pre><code>&lt;dependency>
    &lt;groupId>com.groupon.roboremote&lt;/groupId>
    &lt;artifactId>roboremoteclient&lt;/artifactId>
    &lt;version>0.6.0-b2&lt;/version>
&lt;/dependency>
&lt;dependency>
    &lt;groupId>com.groupon.roboremote.roboremoteclient&lt;/groupId>
    &lt;artifactId>junit&lt;/artifactId>
    &lt;version>0.6.0-b2&lt;/version>
&lt;/dependency></code></pre>

## Examples
***
An example project is provided in the examples/HelloWorld directory.  The directory structure is as follows:

* helloworld - This is the application under test which provides a simple list view and an activity to launch based on pressing an itme in the list view
* helloworldtestrunner - This is the test runner which starts up the RoboRemote HTTP listener and the application under test.
* Tests -
  * SampleTests - This contains the desktop JUnit based RoboRemote Robotium example tests for the HelloWorld project.  Under Tests/src/test is a SampleTests.java which contains a few tests demonstrating various RoboRemote functions.
  * AutomatorTests - This contains the desktop JUnit based RoboRemote UiAutomator example tests for the HelloWorld project.  This also serves as an example of how to call various functions in the UiAutomatorClient TestBase to setup the test and run the application you would like to test.
  * CombinedTests - This contains the desktop JUnit based RoboRemote Robotium/UiAutomator combined test example for the HelloWorld project.  It demonstrates how UiAutomator and Robotium calls can be mixed.

### Compiling the examples

Running "mvn clean install" from the "examples" directory will build the examples.  Be sure to run "mvn clean install" from the root first to compile the latest library versions

### Installing helloworld/helloworldtestrunner

Execute the following from your repository root:

1. adb install examples/HelloWorld/helloworld/target/HelloWorld-1.0.apk
2. adb install examples/HelloWorld/helloworldtestrunner/target/helloworldtestrunner-1.apk

### Executing the examples

Execute the following from examples/HelloWorld/Tests

1. Robotium example: mvn test -Dtest=SampleTests
2. UiAutomator example:
   1. export ROBO_UIAUTOMATOR_JAR=&lt;path to source root>/UIAutomatorServer/target/uiautomatorserver.jar
   2. mvn test -Dtest=AutomatorTests
3. Combined example:
   1. export ROBO_UIAUTOMATOR_JAR=&lt;path to source root>/UIAutomatorServer/target/uiautomatorserver.jar
   2. mvn test -Dtest=CombinedTests


## Building RoboRemote from Source
***
In order to build roboremote from source you need to deploy the maven Android modules using the maven-android-sdk-deployer(https://github.com/mosabua/maven-android-sdk-deployer).  Install at least versions 4.1, 4.2, 4.3 and 4.4 to get started.  After these are installed it is as simple as running "mvn clean install"


## Architecture
***
RoboRemote consists of two main components and several framework specific.  The main components are RoboRemoteServerCommon and RoboRemoteClientCommon.  Robotium specific components are RoboRemoteServer, RoboRemoteClient and RoboRemoteClientJUnit.  The UIAutomator specific components are UIAutomatorServer and UIAutomatorClient.  The entire project is managed by Maven and the components are provided as artifacts.

### RoboRemoteServerCommon
RoboRemoteServer acts as a HTTP interface to arbitrary function calls.  RoboRemoteServer and UIAutomatorServer are specific usages of the component.  It accepts requests and provides responses in JSON format.  The input JSON describes the class, method and method parameters for the function that is being called.

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

RoboRemoteServerCommon(RemoteServer.java contains a function(getTypeEquivalents) that maps parameter types to equivalent parameter types.  This is used to determine if the value that was passed in through JSON(ex: Integer) can be used as an argument to a function that takes a different numerical type.  If you are trying to call a function and it is not working properly then a missing mapping in this function is the likely culprit.

**What do I do with the String version of the View I requested?**

The textual versions of views or class names can be passed back is an argument to a function that requires a class or a view.  An example will be shown in the RoboRemoteClient section of this README.

### RoboRemoteClientCommon
RoboRemoteClientCommon is a java library(jar) that provides the following functionality:

* Device/Emulator functions(start app, close app, clear app data)
* Test logging(using SLF4J and logback)
* Logcat collection
* Event based testing(currently logcat monitoring)
* Failure screenshots

The specific UIAutomator or Robotium implementations provide:
* Robotium Solo emulation
* UIAutomator UiObject, UiDevice, UiSelector and UiScrollable emulation
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

#### UiAutomator Emulation

UIAutomatorClient provides several classes(UiDevice, UiObject, UiSelector, UiScrollable, UiCollection) that provide interfaces to the native classes.  UiDevice provides function calls to all of it's functions.  The other classes provide call(..) methods to map function calls according to class documentation for UiAutomator.

**Example UiAutomator requests**
*Traditional UiAutomator Syntax*:
<pre><code>UiObject cancelButton = new UiObject(new UiSelector().text("Cancel").className("android.widget.Button"));
if (cancelButton.exists())
    cancelButton.click();</code></pre>
*UiAutomator client request*:
<pre><code>UiObject cancelButton = new UiObject(new UiSelector().call("text", "Cancel").call("className", "android.widget.Button"));
if (cancelButton.call("exists").getBoolean(0))
    cancelButton.call("click");</code></pre>


#### Arbitrary Function Calls

RoboRemoteClientCommon also provides a method(in com.groupon.roboremote.RoboRemoteClient.Client) to call any function in a static/non-static class.  The map method is defined as map(String className, String method_name, Object … parameters).  If the call is successful then it returns a JSONArray of the results.  If the method only has a single return value then it will be in the first element of the JSONArray.  Elements in lists/arrays are returned in the same position in the JSONArray as they would be for the normal function call.

**Example function call**

*Traditional method call in a test*:
<pre><code>java.lang.System.exit(0);</code></pre>

*RoboRemote client request*:
<pre><code>Client.map("java.lang.System", "exit", 0)</code></pre>

***Note about function calls***: You can call methods in static/non-static classes, but cannot call methods in already instantiated classes.  In order to use already instantiated classes you will have to add hook methods in your test runner(explained in the Getting Started section of this README).  The only exceptions to this are the Robotium Solo class and the test instrumentation.  Robotium is pre-instantiated and can be referenced as "solo" in a map call.  The test instrumentation can be referenced as "testclass" in a map call.

**Complex function calls**
Robotium 4.x introduced some more complex function calls for web elements.  The functions take a By parameter.  To support this the system provides a method of storing and retrieving objects.  An example is:

*Traditional Syntax*:
<pre><code>solo.waitForWebElement(By.textContent("myText"))</code></pre>

*RoboRemote client request*:
<pre><code>new QueryBuilder().map("com.jayway.android.robotium.solo.By", "textContent", "myText").storeResult("byMyText").execute();
Client.map("solo", "waitForWebElement", QueryBuilder.getStoredValue("byMyText"));
</code></pre>

**Class Instantiation**
RoboRemoteClientCommon provides a method to do paramaterized class instantion.

*Traditional Syntax*:
<pre><code>UiObject myObject = new UiObject(mySelector);</code></pre>

*RoboRemoteClientCommon Syntax*(mySelector is already a stored value in this example):
<pre><code>new QueryBuilder().instantiate("com.android.uiautomator.core.UiObject", QueryBuilder.getStoredValue("mySelector")).execute();</code></pre>

##### Fields

You may have a neeed to retrieve content from fields in a class.  This can be done using the mapField(String className, String) function instead of the map(…) function.

**Example field accessor**

*Traditional field access in Robotium test*:
<pre><code>solo.DELETE</code></pre>

*RoboRemote client request*:
<pre><code>int DELETE = Client.getInstance()mapField("solo", "DELETE").getInt(0)</code></pre>

*Access inner class field("android.os.Build.VERSION.SDK_INT")*:
<pre><code>int apiLevel = Client.getInstance()mapField("android.os.Build$VERSION", "SDK_INT").getInt(0)</code></pre>

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

## TODO/Limitations
***
1. Add compatibility for Android 2.1 - 2.2

## Links
***
**Robotium** - <http://code.google.com/p/robotium/>

**JUnit** - <http://www.junit.org/>

**Frank** - <https://github.com/moredip/Frank>
