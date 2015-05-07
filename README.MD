# MBAMS

MBAMS is a Mobile-based Attendance Management System.

  - Web Application for instructors (server) <br />
     The web application is written in `Java` using `Play Framework`.
     Based on [Computer Database Java](https://github.com/typesafehub/activator-computer-database-java)
  - Android Application for attendants (client)
 
##Screenshots

![Web Application](http://www.pictureshack.us/images/28094_students-db.png)

![Android Application signup](http://www.pictureshack.us/images/38828_Screenshot_2015-05-03-21-39-11.png)

![Android Application signin](http://www.pictureshack.us/images/10010_Screenshot_2015-05-03-21-42-56.png)

## Version
1.0

##Features:
* Web application consists of a MySQL database, with full CRUD operations.
* The instructor uses the web app to mange his attendants attendances by selecting the lecture number every lecture.
* The Android Application can connect to the web app.
* The first time, the attendant has to sign up in the system just by entering his ID.
* Every Lecture the attendant can submit his attendance just by one click!
* The web app responds to the android app, so that the attendant can make sure he had submitted his attendance successfully.
* The android app can't submit attendance to more than one attendant. ONLY ONE!

## How to build MBAMS

###For the web application:
1. You need MySQL installed
2. Create a database with the name `students`
3. Clone the repo: <br />
    ```sh
       $ git clone git@github.com:CATReloaded/MBAMS.git MBAMS
    ```
	
4. Change the configuration to match yours: <br />
	```sh
       $ nano MBAMS/server/conf/application.cong
    ```
change `db.default.user` and `db.default.pass` to your user & password for the db.
5. Run the app using: <br />
    ```sh
       $ ./activator run
    ```

###For the Android application:

1. Install the following software: <br />
    Android SDK: http://developer.android.com/sdk/index.html <br />
    Gradle: http://www.gradle.org/downloads  <br />
    Android Studio: http://developer.android.com/sdk/installing/studio.html  <br />

2. Import the project in Android Studio
3. Change the configurations strings values:

	String | Value
	------------ | -------------
	`serverURL` |  The IP of your machine that runs the web app following with the port number `9000`
	`wifiName` | The name of the wifi that you will create for the attendants.
	`wifiPassword` | The password of above wifi
	`siginURL` | The route of the sign in in the web app.<br /> _Don't change it if you didn't change it in the web app._
	`submitURL` | The route of the submit attenance in the web app. <br /> _Don't change it if you didn't change it in the web app._

##License:

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
