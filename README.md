# NutriFitsta

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
NutriFitsta is an app that give anyone interested in health & fitness a platform to keep track of their journey and share their progress with friends and other users. Users can log their lifestyle (i.e. videos or pictures of exercise and meals) into their profile with customizable privacy settings and engage with other users' content. The features for log posts will be detailed to contain information such as calories, recipes, media, etc.

### App Evaluation
- **Category:** Health & Fitness, Social Networking 
- **Mobile:** This product is uniquely mobile due to its featuress for instant logging of workouts and food upon completion. Uses the camera to take pictures of food or at the exercise facility (gym or even home setup!) and can tag locations for each event. Also utilizes notifications for reminders/motivation and when others engage with your profile.
- **Story:** Introducing a community and social aspect to promoting health lifestyles. Motivates users to stay on track with their fitness/diet journey by adding in external encouragement from friends and helps users find new improvements to their lifestyle by giving them access to other user's content (i.e. a new recipe or workout video) and filtering their feed for relevant material.
- **Market:** Anyone interested in health & fitness ranging from fitness influencers to beginners looking to build a gym plan to users trying to track their diet and meet nutrient goals.
- **Habit:** Users are logging their daily activities as they are being completed and will be using the app multiple times throughout the day. The app can also be used past logging times to view other user content and gain inspiration. 
- **Scope:** V1 will let users create simple logs of their daily exercise/diet, follow other users, and interacts with their posts. V2 will allow users to set fitness/diet goals and will incorporate a reminder/reward system. V3 will introduce an "explore" page showing other users with similar goals to promote a community. V4 will allow the user to filter their feed/followers based on a specific fitness/diet goal category.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can login 
* User can create a new account
* Users can log activities to their profile
* Users can upload/take 1 picture of their activities for their log
* Users can add a youtube link for recipe/workout
* Users can view their own profile
* Users can see other users' post on a timeline feed
* Users can like a post
* * Users can save posts and see all of their saved posts in a screen

**Optional Nice-to-have Stories**
* Users can filter their timeline (i.e. only exercise posts, only food posts, only exercise posts under 10 minutes, etc.)
* Users can search for other users and see their profiles
* Users can easily search up nutritional information when adding details for each post (3rd party API)
* Users can set goals for themselves and recieve notifications/reports on their progress
* Users can receive notifications of others interacting with their profile
* Users can comment on a post
* Users can upload multiple images/videos
* Users will have an explore page of other user profiles with similar goals

### 2. Screen Archetypes

* Start Screen
* Login Screen
    * User can login 
* Registration Screen
    * User can create a new account
* Timeline
    * Users can see the other's posts on a timeline feed
    * Users can like a log post
* Creation
    * Users can log activities to their profile 
    * Users can upload/take pictures/videos of their activities for their log
    * Users can search up and add nutritional information about their meals
* Search
    * Users can search for other users
    * Users can see all their saved posts of others
* Profile
    * Users can see all the logs of their own


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home Timeline Feed
* Personal Profile
* Search/Saved

**Flow Navigation** (Screen to Screen)

* Login Screen
    * Registration Screen
    * Home Timeline Feed 
* Registration Screen
    * Home Timeline Feed
* Timeline
    * Can navigate to the poster's profile
* Search/Saved
    * Can navigate to the user's profile
    * Can open detailed view of a saved post
* Profile
    * Can open Creation Screen
* Creation
    * Open camera
    * After post is created, go back to Profile


## Wireframes
<img src="https://github.com/sxiao101/NutriFitsta/blob/master/wireframe_sketches.jpg" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
### Models
#### User
| Property | Type | Description |
| ------------- | ------------- | ------------- |
| objectId  | String  | unique id for the user (default field) |
| username | String  | username |
| password | String  | password for user login |
| saved | Array of Pointers to Posts  | collection of posts that the user has saved |

#### Post
| Property | Type | Description |
| ------------- | ------------- | ------------- |
| objectId  | String  | unique id for the post (default field) |
| createdAt  | Date  | time for when the post was created (default field) |
| type  | Boolean  | "food" for a food type; "fitness" for a fitness post |
| foodpost  | String  | objectId of the detailed food post |
| fitnesspost  | String  | objectId of the detailed fitness post |

#### FoodPost
| Property | Type | Description |
| ------------- | ------------- | ------------- |
| objectId  | String  | unique id for the post (default field) |
| createdAt  | Date  | time for when the post was created (default field) |
| user  | Pointer to User  | post creator |
| category  | String  | breakfast, lunch, dinner, etc. |
| title  | String  | post title by creator |
| nutrition  | Number  | number of calories for the food post |
| recipe  | Array of Strings  | list of ingredients used |
| likesCount | Number  | number of likes for the post |
| likers | Array of Pointers to Users | collection of users who liked the post |
| image | File  | image the creator posts |
| description  | String  | description of the post by creator |
| video  | String  | YouTube link that creator can add |

#### FitnessPost
| Property | Type | Description |
| ------------- | ------------- | ------------- |
| objectId  | String  | unique id for the post (default field) |
| createdAt  | Date  | time for when the post was created (default field) |
| user  | Pointer to User  | post creator |
| title  | String  | post title by creator |
| duration  | Number  | length of workout |
| workout  | Array of Strings  | list of exercises done |
| likesCount | Number  | number of likes for the post |
| likers | Array of Pointers to Users | collection of users who liked the post |
| image | File  | image the creator posts |
| description  | String  | description of the post by creator |
| video  | String  | YouTube link that creator can add |

#### PostActions
| Property | Type | Description |
| ------------- | ------------- | ------------- |
| objectId  | String  | unique id for the post (default field) |
| postId  | String  | objectId for a post |
| user  | Pointer to User  | the person who executed actions on the post |
| saved  | Boolean  | "true" if the user saved the post |
| liked  | Boolean  | "true" if the user liked the post |



### Networking
* Home Timeline Screen
    * (Read/GET) Query all posts
    * ```// Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // Specify the object id
        query.include(Post.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> posts, ParseException e) {}
        });```
    * (Create/POST) Create a new like on a post
    * ```// increase number of likes for the post
        post.put("likesCount", post.getLikes()+1);
        // add user to the array of users who liked the post
        post.put...
        post.saveInBackground();``
    * (Delete) Delete existing like
* Search/Saved Screen
    * (Read/GET) Query logged in user object
    * (Read/GET) Query all posts saved by logged in user
    * (Read/GET) All users
    * (Read/GET) All posts and info of searched up user object
* Profile Screen
    * (Read/GET) Query logged in user object
    * (Read/GET) Query all posts where user is author
* Creation Screen
    * (Create/POST) Create a new post object
- [OPTIONAL: List endpoints if using existing API such as Yelp]

* https://spoonacular.com/food-api
