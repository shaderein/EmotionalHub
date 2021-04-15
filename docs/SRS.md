# Software Requirements Specificatins

## Problem Statement 

> Nowadays, access to information is easier than ever. Whenever you open Facebook, or your favorite news app, there are always new posts waiting for you. **However, such social media is usually designed to display the posts that makes users spend the most time on the platform, but not to show the posts that benefits the users in terms of letting users read what they want or what makes them feel better (in their own definition of feeling better) by taking users current feelings and intentions in to consideration.** 

> (Optional) We experience various kinds of emotions every day, but sometimes it's difficult for others, even friends and family, to understand and resonate with our feelings. Have you ever been in a situation, when you are feeling depressed, but don't want to tell your mom about it because the only thing she can do is to be sad with you? Have you ever resisted the excitement to share a great achievement with your friends because they may be jealous? However, we, as humans, all need proper emotional outlets that clears up our minds and make our lives easier, and we believe technology can help with it.

## Potential Clients

> Broadly, potential users of this software include anyone with a phone or computer with access to the internet. It would be particularly interesting to people with emotional needs, such as those who feel stressed and want to be soothed, those who feel lazy and want to find motivation, or simply those who want to share their feelings. It won't be a professional platform for treating emotional diseases, but will be an emotional hub for people who want to find internal peacefulness. The target age group of this app will be above 18, because those people will be likely to have emotional concerns and also to try out new platforms.

## Proposed Solution

> Based on the goal to "provide an emotional hub," we propose an app that takes the form of a social platform, where people can sign in and make posts. When they start the app, users will be asked to select an intention that best describes their emotional need. Those include <em>cheer me up, excite me; chill, relax; inspire me, motivate me; make me laugh; see opinions; care for my emotion </em> (where users can in turn choose their emotion) <em>; skip</em>.

> We will have a recommendation algorithm to let users see posts that are designed for their feelings and adjusted for their interests. The posts will be tagged by our **sentiment analysis algorithm** that is trained on text data and analyzes the text component of the posts to detect the underlying emotions of the posts. (We can consider emotion detection models on images in the future.) Other machine learning models may apply such as a "joke detector." We will then match those tags with the intentions that users select. We may also ask users to provide some tag (e.g. is "inspiring") for posts they write in the case that the algorithm doesn't work well. As a plus, we can keep track of the user's emotion change and change our posts accordingly. Details on how to adjust posts for users' interests is still to be decided.

> Contents of the posts may be text, pictures, videos, etc. Users will see posts that they desire. If users know what they want (e.g. wants to be more excited, be encouraged) by browsing on our platform, the platform would show the according posts based on our analysis algorithm. Or, if the users do not have a clear intention, they can indicate their current feelings or emotions and the platform would show appropriate posts (we still need to define what we mean by appropriate here). If they choose not to select an intention, they will see all the posts on the platform. Users with more positive feelings will be invited to make posts for others to see. Anyone can make posts, and initial posts will be written (or obtained from freely available online sources) by the software team.

> We can also make a forum that allows people to post threads about their feelings and why, and ask for advice from others. In this way, users will have a space to freely express themselves and have their emotions understood.

## Functional Requirements

### Must have

> As a user, I will be able to:
> 1. login.
> 2. select my emotional intention.
> 3. see interesting contents that address my feeling (requires a post recommendation system).
> 4. make posts about anything I like.
> 5. be able to upload images, in addition to plain text.
> 6. see a history of the posts that I've made.
> 7. comment on people's posts.
> 8. change my emotional intention when it does change.
> 9. when making a post, be able to remain anonymous so that I won't need to worry about identity issues.

### Nice to have

> As a user, I also want to be able to:
> 1. like or dislike other people's posts.
> 2. save my favorite posts.
> 3. have user profiles so that I can know more about other people and let them know me.
> 4. open a thread on an emotionally-aware forum so that I can ask questions or express myself.
> 5. follow up on a thread so that I can answer other people's questions, help with their concerns, share their happiness, etc.
> 6. add other users as friends and chat with them privately.
> 7. make private threads in the forum where I can chat with selected users privately.
> 8. log in with a strong authentication system so that my information is protected.
> 9. search for posts and forum threads that interest me.
> 10. see the website on my phone.


## Software Architecture

> This will be a Web application. If it turns out well, we can consider moving it to mobile.

> It will conform to the Client-Server software architecture. That is, a client (whoever is visiting our web app) can send and receive data to an API on the server, when they interactive with the application, for example, see and make posts.

> Software that we will use for backend include Java, Gradle build tool, SparkJava API framework, and MongoDB. For frontend we will use HTML, CSS, and Javascript.

## User Stories

> After logging in, the user would be prompted to a page where they can select one of "cheer me up," "chill," "inspire me," "care for my emotion" (where they in turn need to select an emotion), "skip," etc. They will then see contents (text, image, or videos) placed in blocks, posted by other users, which are taylored towards the intentions they selected, using our machine learning algorithm. They would be able to make posts about anything in the empty blocks.

> The user would also be able to open a thread on an emotionally-aware forum so that they can ask questions or express myself. They can follow up on a thread to answer other people's questions, help with their concerns, share their happiness, etc.

## Wireframes

> Please check the Wireframes.zip file within the docs folder for complete wireframes with detail.

> Here is the overview of all wireframes:
![Overview #1](https://github.com/jhu-oose/2020-fall-group-unicorns-of-love/blob/master/docs/Overview%20%231.png?raw=true)
![Overview #2](https://github.com/jhu-oose/2020-fall-group-unicorns-of-love/blob/master/docs/Overview%20%232.png)
