# SensorStrike

## Inspiration
Counter Strike: Global Offensive is perhaps the most realistic simulation of warfare available today. This battle simulator pits keyboard warriors against each other, and the ones more versed in the art of war usually prevailed. Although CS:GO was the pinnacle of human achievement, we believed that we could improve upon the immersive realism of this gem. The common mouse (of the electronic variety) has long been associated with computers and computer gameplay, but if you stop to think about it, its interface is unfun and uninteractive. Our idea, SensorStrike, eliminates this barrier by effectively replacing the obsolete gaming mouse with something far more natural. Admit it, none of us can take our hands off of our smartphones, so we might as well use them for everything - including gaming. We've brought the future to the present, and it is our wish to share this monumental moment with you. Consider this your invite.

## What it does
This app allows you to emulate keyboard strokes and mouse clicks with your smartphone. Using the orientation sensor on the phone, we were (impressively) able to control the mouse cursor with a mere mobile phone. For this Hackathon, we have specifically tailored our app for CS:GO. This will enhance the gaming experience for pros and noobs alike.

## How we built it
Used Android Studio for game controller platform and Java to run the control server on our PC. The Android device is able to talk to the computer through the use of Bluetooth.

## Challenges we ran into
- Finding a good Bluetooth library to use for the server side program (most online examples and tutorials were unbelievably outdated)
- Spending a lot of time backlogged researching over data outputs from semi-deprecated Android sensor functionalities
- Developing an efficient noise reducing algorithm that provides the least amount of shuddering movement and semi-optimal smooth gameplay 

## Accomplishments that we're proud of
- Learned to communicate between different devices through Bluetooth.
- Made a mobile app that provided a new style of gameplay for and FPS shooter.

## What we learned
- We learned how to set up with a server using Bluetooth and connect via ports.
- We also learned how to use several useful interface libraries, and how to develop noise reducing algorithms.
- Most of all we found that a lot of the deprecated functionalities in Android should stay deprecated.

## What's next for SensorStrike.
We hope to offer native support for games other than CS:GO in the future. We also plan to increase the accuracy of our mouse tracking system, although we acknowledge we may be limited by the inherent inaccuracies of mobile sensors. Most importantly, we hope to expand on the feature set of SensorStrike by implementing new and creative ways of supporting new key bindings and controls.
