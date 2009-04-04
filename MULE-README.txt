

WELCOME
=======

Congratulations you have just created a new Mule transport!

This wizard created a number of new classes and resources useful for new Mule transports.  Each of the created files
contains documentation and _todo_ items where necessary.  You'll need to look at each of the classes and other files and
address the _todo_ items in the files. Here is an overview of what was created.

./pom.xml:
A maven project descriptor that describes how to build this project.  If you enabled this project for the
MuleForge, this file will contain additional information about the project on MuleForge.

./assembly.xml:
A maven assembly descriptor that defines how this project will be packaged when you make a release.

./LICENSE.txt:
The open source license text for this project.

-----------------------------------------------------------------
./src/main/java/org/mule/transport/saaj/i18n/SaajMessages.java:

The SaajMessages java class contains methods for access i18n messages embedded in your java code.

-----------------------------------------------------------------
./src/main/resources/META-INF/services/org/mule/i18n/saaj-messages.properties

These message properties contain i18n strings used by SaajMessages.java.

    
    -----------------------------------------------------------------
./src/main/resources/META-INF/mule-saaj.xsd

The configuration schema file for this module. All configuration elements should be defined in this schema.

-----------------------------------------------------------------
./src/main/resources/META-INF/spring.schemas

Contains a mapping of the Namespace URI for this projects schema.

-----------------------------------------------------------------
./src/main/resources/META-INF/spring.handlers

Contains a mapping of the namespace handler to use for the schema in this project.

-----------------------------------------------------------------
./src/main/java/org/mule/transport/saaj/config/SaajNamespaceHandler.java

The implmentation of the namespace handler used to parse elements defined in mule-saaj.xsd.
    
-----------------------------------------------------------------
./src/main/java/org/mule/transport/saaj/SaajConnector.java

The connector for this transport. This is used for configuing common properties on endpoints for this transport
and initialising shared resources.

-----------------------------------------------------------------
./src/main/java/org/mule/transport/saaj/SaajEndpointURIBuilder.java

The class responsible for parsing custom endpoints for this transport.

-----------------------------------------------------------------
./src/main/java/org/mule/transport/saaj/SaajInboundTransformer.java

This transformer should convert the inbound message into a type consumable by Mule.  For example, in the case of JMS this
class would would convert a JMSMessage to a String, object, Map, etc depending on the time of message.  If your transport
does not have a specific message type you do not need this class (see SaajMessageAdapter).

-----------------------------------------------------------------
./src/main/java/org/mule/transport/saaj/SaajOutboundTransformer.java

This transformer should convert the otbound message into a type supported by the underlying technology.  For example,
in the case of JMS this class would would convert a MuleMessage to a JMSMessage.  If your transport
does not have a specific message type you do not need this class.

-----------------------------------------------------------------
./src/main/java/org/mule/transport/saaj/SaajMessageAdapter.java

This class is used to wrap inbound messages and access the data in the message in a unified way.  The MessageAdapter provides
access to the payload, message headers and attachments.

-----------------------------------------------------------------
./src/main/java/org/mule/transport/saaj/SaajMessageDispatcher.java

This part of the transport responsible for outbound endpoints (client).  This class should implement the logic needed to
dispatch messages over the underlying transport.

-----------------------------------------------------------------
./src/main/java/org/mule/transport/saaj/SaajMessageDispatcherFactory.java

The factory used to create SaajMessageDispatcher instances.

-----------------------------------------------------------------
./src/main/java/org/mule/transport/saaj/SaajMessageReceiver.java

This part of the transport responsible for inbound endpoints.  This class should implement the logic need to
receive messages from the underlying transport.  Mule supports polling receivers, that pull events from the transport, but
users can implement listener interfaces to have events pushed to the receiver.

-----------------------------------------------------------------
./src/main/java/org/mule/transport/saaj/SaajTransactedMessageReceiver.java

This class should implement the logic need to receive messages from the underlying transport using a transaction.
If the underlying transport does not support Transactions, this receiver is not required.

-----------------------------------------------------------------
./src/main/java/org/mule/transport/saaj/SaajMessageRequester.java

This part of the transport responsible for making individual requests to receive an event from the transport.  This class
should implement the logic needed to make this type of request via the transport.

-----------------------------------------------------------------
./src/main/java/org/mule/transport/saaj/SaajMessageRequesterFactory.java

The factory used to create SaajMessageRequester instances.

-----------------------------------------------------------------
./src/main/java/org/mule/transport/saaj/SaajTransaction.java

If the underlying transport supports transactions, this class wraps the transaction so that it can be accessed by Mule.

-----------------------------------------------------------------
./src/main/java/org/mule/transport/saaj/SaajTransactionFactory.java

The factory used to create SaajTransaction instances.


TESTING
=======

This  project also contains test classes that can be run as part of a test suite.

-----------------------------------------------------------------
./src/test/java/org/mule/transport/saaj/SaajTestCase.java

This is an example functional test case.  The test will work as is, but you need to configure it to actually test your
code.  For more information about testing see: http://www.mulesource.org/display/MULE2USER/Functional+Testing.

-----------------------------------------------------------------
./src/test/resources/saaj-functional-test-config.xml

Defines the Mule configuration for the SaajTestCase.java.

    -----------------------------------------------------------------
./src/test/java/org/mule/transport/saaj/SaajNamespaceHandlerTestCase.java

A test case that is used to test each of the configuration elements inside your mule-saaj.xsd schema file.

-----------------------------------------------------------------
./src/test/resources/saaj-namespace-config.xml

The configuration file for the SaajNamespaceHandlerTestCase.java testcase.
    
ADDITIONAL RESOURCES
====================
Everything you need to know about getting started with Mule can be found here:
http://www.mulesource.org/display/MULE2INTRO/Home

For information about working with Mule inside and IDE with maven can be found here:
http://www.mulesource.org/display/MULE2INTRO/Setting+Up+Eclipse

Remember if you get stuck you can try getting help on the Mule user list:
http://www.mulesource.org/display/MULE/Mailing+Lists

Also, MuleSource, the company behind Mule, offers 24x7 support options:
http://www.mulesource.com/services/subscriptions.php

Enjoy your Mule ride!

The Mule Team

--------------------------------------------------------------------
This project was auto-genrated by the mule-project-archetype.

artifactId=mule-transport-saaj
description=Transport SOAP messages using SAAJ
muleVersion=2.1.2
hasCustomSchema=y
hasBootstrap=${hasBootstrap}
projectType=${projectType}
packagePath=org/mule/transport/saaj
forgeProject=y
transports=http,https
modules=client,xml

version=1.0-SNAPSHOT
groupId=org.mule.transports
basedir=/Users/johndemic/Development/Personal/mule/saaj-transport
--------------------------------------------------------------------