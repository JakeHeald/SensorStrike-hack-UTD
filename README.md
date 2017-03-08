# SensorStrike

- Devpost link: https://devpost.com/software/hack-utd

- CS:GO Demo Play: https://www.youtube.com/watch?v=JO4VNabCiHM

## How to run the code
1. Fill in [Mac Address](https://kb.netgear.com/1005/How-to-find-a-MAC-address) in *mobile-app-controller-code/../MainActivity.java* for the corresponding Bluetooth Server running device (Usually a computer).
2. Pair Bluetooth between your Bluetooth server device and your Android device that contains Orientation Data (You can find out if it works by testing on any  given [Android Sensor App](https://play.google.com/store/search?q=sensor%20apps%20for%20android&c=apps&hl=en)).
3. Build *mobile-app-controller-code/* in Android Studio onto your compatible Android device so your Android device will start sending out a connection request once you press the "Ready" button.
4. Execute Server-side code on your favorite IDE in order to start receiving a server connection request from Android device.
5. Enter CS:GO and start your game, and aim your phone towards your computer screen, phone screen oritned to the leftfacing to the left.
6. Press the "Ready" button on your phone during the loading screen of CS:GO to start an experience of playing an FPS game without a mouse or keyboard.

## How to Play
- After opening the app and establishing a connection with your computer, you should see a controller with several buttons (Quit, R) and an azure circle in the center of a blackened circle (Joystick).
- Moving up on the Joystick represents 'W', Left for 'A', Right for 'D', and Down for 'S'.
- Moving diagonally on the joystick will press down two of the corresponding keys.
- Pressing 'R' will Reload your weapon.
- Pressing 'Quit' will end your connection with the computer.

## Inspiration
Counter Strike: Global Offensive is perhaps the most realistic simulation of warfare available today. This battle simulator pits keyboard warriors against each other, and the ones more versed in the art of war usually prevailed. Although CS:GO was the pinnacle of human achievement, we believed that we could improve upon the immersive realism of this gem. The common mouse (of the electronic variety) has long been associated with computers and computer gameplay, but if you stop to think about it, its interface is unfun and uninteractive. Our idea, SensorStrike, eliminates this barrier by effectively replacing the obsolete gaming mouse with something far more natural. Admit it, none of us can take our hands off of our smartphones, so we might as well use them for everything - including gaming. We've brought the future to the present, and it is our wish to share this monumental moment with you. Consider this your invite.

## What it does
This app allows you to emulate keyboard strokes and mouse clicks with your smartphone. Using the orientation sensor on the phone, we were (impressively) able to control the mouse cursor with a mere mobile phone. For this Hackathon, we have specifically tailored our app for CS:GO. This will enhance the gaming experience for pros and noobs alike.

## How we built it
Used Android Studio for game controller platform and Java to run the control server on our PC. The Android device is able to talk to the computer through the use of Bluetooth.

## Challenges we ran into
- Finding a good Bluetooth library to use for the server side program (most online examples and tutorials were unbelievably outdated).
- Spending a lot of time backlogged researching over data outputs from semi-deprecated Android sensor functionalities.
- Developing an efficient noise reducing algorithm that provides the least amount of shuddering movement and semi-optimal smooth gameplay .

## Accomplishments that we're proud of
- Made a mobile app that provided a new style of gameplay for and FPS shooter.
- Learned to communicate between different devices through Bluetooth.
- Created a product that could be formatted to an infinite number of games.

## What we learned
- We learned how to set up with a server using Bluetooth and connect via ports.
- We also learned how to use several useful interface libraries, and how to develop noise reducing algorithms.
- Most of all we found that a lot of the deprecated functionalities in Android should stay deprecated.

## What's next for SensorStrike.
We hope to offer native support for games other than CS:GO in the future. We also plan to increase the accuracy of our mouse tracking system, although we acknowledge we may be limited by the inherent inaccuracies of mobile sensors. Most importantly, we hope to expand on the feature set of SensorStrike by implementing new and creative ways of supporting new key bindings and controls.

## Online Resources
- [Bugstick - Android Java UI Joystick Interface on Github](https://github.com/justasm/Bugstick) by {justasm](https://github.com/justasm)

- [Noise Reduction Algorithm Reference](https://terpconnect.umd.edu/~toh/spectrum/Smoothing.html)

- [Bluetooth Java Server Library](http://snapshot.bluecove.org/distribution/download/2.1.1-SNAPSHOT/2.1.1-SNAPSHOT.62/)

- [StackOverflow: Communication Through Bluetooh from Java server to Android Client](http://stackoverflow.com/questions/10929767/send-text-through-bluetooth-from-java-server-to-android-client?rq=1)

- [Java Robot API Docs](https://docs.oracle.com/javase/7/docs/api/java/awt/Robot.html)

## Awards
- Awarded "Mobile-Track" Award at HackUTD 2017 
