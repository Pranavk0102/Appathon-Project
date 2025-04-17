# Jewellery Comparison App

## Overview

The Jewellery Comparison App is an Android application that allows users to browse and compare different jewellery items. Administrators can add new jewellery details, including images and specifications, through a dedicated admin interface. Users can view the available jewellery, select items of interest, and compare their details in a table format.

## Features

**User Features:**

* **Browse Jewellery:** View a list of available jewellery items, including images and basic details.
* **Select for Comparison:** Choose up to four jewellery items to compare.
* **Comparison Table:** View a detailed comparison of selected items, including product type, gold karatage, gold weight, diamond/gemstone weight, total price, labour charges, and design code.
* **User Authentication:** Secure login and logout functionality for users.

**Admin Features:**

* **Add New Jewellery:** Upload images and enter specifications for new jewellery items.
* **Real-time Updates:** Newly added jewellery items are immediately reflected in the user's view.

## Technologies Used

* **Android SDK:** For building the native Android application.
* **Java:** The primary programming language.
* **Firebase:**
    * **Authentication:** For user login and logout.
    * **Realtime Database:** To store and retrieve jewellery data in real-time.
    * **Storage:** To store jewellery images.
* **Glide Library:** For efficient image loading and caching from URLs.
* **Picasso Library:** Used in the comparison activity for loading images into the comparison table.
* **Android Jetpack Libraries:** For enhanced UI and background operations.

## Setup Instructions

1.  **Clone the Repository:**
    ```bash
    git clone [repository_url]
    cd JewelleryComparisonApp
    ```

2.  **Set up Firebase Project:**
    * Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
    * **Enable Authentication:** Set up the desired authentication methods (e.g., Email/Password).
    * **Create Realtime Database:** Create a Firebase Realtime Database. Ensure your security rules allow read access for users and write access for authenticated administrators.
    * **Create Storage Bucket:** Create a Firebase Storage bucket to store images. Set up your security rules to allow read access and authenticated write access.
    * **Download `google-services.json`:** Add your Android app to the Firebase project and download the `google-services.json` file. Place this file in the `app/` directory of your Android project.

3.  **Configure Android Project:**
    * Open the project in Android Studio.
    * **Add Firebase SDKs:** Ensure the necessary Firebase SDKs (Authentication, Realtime Database, Storage) are added to your `app/build.gradle` file. Android Studio usually handles this when you add Firebase through the Tools menu.
    * **Add Dependencies:** Verify that the dependencies for Glide and Picasso are present in your `app/build.gradle` file:
        ```gradle
        implementation 'com.github.bumptech.glide:glide:4.12.0'
        annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0'
        implementation 'com.squareup.picasso:picasso:2.71828'
        ```
        (Use the latest stable versions if available)
    * **Sync Gradle:** Sync your Gradle project in Android Studio (`File > Sync Project with Gradle Files`).

4.  **Run the Application:** Build and run the application on an Android emulator or a physical device.

## Usage

**For Users:**

1.  Launch the application and log in with your credentials.
2.  The main screen will display a list of available jewellery items.
3.  Tap the "Select" button next to items you want to compare (you can select up to four).
4.  Once you have selected the desired items, tap the "Compare Selected Items" button at the bottom.
5.  A new screen will show a table comparing the specifications of the selected jewellery.
6.  You can log out using the "Logout" button.

**For Administrators:**

1.  Launch the application and navigate to the admin activity (this might be a separate entry point or accessible after admin login - refer to the app's specific navigation).
2.  On the admin screen, you will find fields to enter details for a new jewellery item: product type, gold karatage, gold weight, diamond weight, total price, labour charges, and design code.
3.  Tap the "Upload Image" area to select an image from your device's gallery.
4.  Once all details are entered and an image is selected, tap the "Add Jewel" button to upload the information to Firebase.
5.  The newly added jewellery will be visible to users in real-time.
