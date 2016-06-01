# Bold -- An Android app for the Bold Bands
====================================

## DESCRIPTION

Bold is an Android app for the Bold Bands: a low cost, disposable diagnostic wearable platform to improve blood pressure management.

With Bold, you can
* See your personalized health report on your phone.
* Email physicians your report without effort.
* Receive automatic reminders to record your blood pressure.

Follow us on http://www.bolddiagnostics.com/.

## SYSTEM REQUIREMENTS

- Android API 23
- Android Studio 2.1

## Accessing the code

Our code can be downloaded from GitHub by cloning our public repository using the following link: 

                                    https://github.com/eecs394-s16/bold1

## Understanding the code

Our file structure is that of a typical Android project. The essential classes and logic code needed for the functioning of the app can be found in the following directory - app/src/main/java/com/collinbarnwell/bold1

We have a few different classes in our code. A list and description of them can be found below:

1)	AddDataPoint.java - Allows the user to log one set of readings and save to the database

2)  DataBaseContract.java - Creating the class that represents the database itself 

3)  DataBaseHelper.java - A class consisting of a number of functions that allow you to interact with the database 

4)  MainActivity.java - 

5)  Profile.java - A class to manage the user's profile

6) UtilClass.java - A set of functions to make it easier to work with the rest of the code 

## Existing features

1) The app allows users to save a list of personal details 

2) You can enter in your body details like blood pressure, heart rate, food intake, etc

3) The app takes blood preasure and heart data to display them in a graph 

4) You can tab points on the graph and view the information represented by them 

5) User's can email a summary of their information to their doctor 

## KNOWN BUGS AND ISSUES

- The app is not compatible with APIs < 23
- The app does not show a warning when the user enter their blood pressure without entering their user information first.
- The app does not support an alarm manager. The alarm manager is still in work in "notifications" branch.
- We've commented out the code to download the pdf report to the phone. It's not fully functiona yet.

## FEATURES TO ADD IN FUTURE

- Integration with Bold Bands
- Downloadable from Play Store
- Backward compatibility with Lollipop, KitKat, and Jellybean.
- Integrate with health provider database for patient information.
- Warn the physician if he/she hasn't been entering blood pressure for a long period of time.

## ACKNOWLEDGEMENTS

This app is developed by [Collin Barnwell](https://github.com/collinbarnwell), [Jerry Li](https://github.com/jerryli27), [Nour Alharithi](https://github.com/NourAlharithi), [Shrivant Bhartia](https://github.com/SV1007), [Yicheng Wang](https://github.com/SHvsMK), [Zavier Henry](https://github.com/ZavierHenry). It a project for 2016 Spring EECS 394: Software Project Management at Northwestern University. We received lots of help from [Kyle Miller](info@bolddiagnostics.com) and [Andrew Wu](jungen.wu@northwestern.edu). We also would like to thank [Prof. Riesbeck](http://cs.northwestern.edu/~riesbeck/) for teaching us how to build a good team and how to be agile coders.
