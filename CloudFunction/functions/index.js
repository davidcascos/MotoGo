const functions = require('firebase-functions');
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendLikeNotification = functions.firestore.document('Likes/{id}')
  .onCreate(async (snapshot) => {
    const userWhoLikes = await admin.firestore().collection('Users').doc(snapshot.data().userId).get();
    const post = await admin.firestore().collection('Posts').doc(snapshot.data().postId).get();

    if (userWhoLikes.data().id === post.data().userId) {
      console.log('Like notification not sent because it is the same user of the post');
    } else {
      const payload = {
        notification: {
          title: `${userWhoLikes.data().username} liked your post`,
        }
      };

      const tokenToNotificate = await admin.firestore().collection('Tokens').doc(post.data().userId).get();
      await admin.messaging().sendToDevice(tokenToNotificate.data().token, payload);
      console.log(`Like Notifications have been sent from: ${userWhoLikes.data().username} to: ${tokenToNotificate.id}`);
    }
  });

exports.sendCommentNotification = functions.firestore.document('Comments/{id}')
  .onCreate(async (snapshot) => {
    const userWhoComments = await admin.firestore().collection('Users').doc(snapshot.data().userId).get();
    const post = await admin.firestore().collection('Posts').doc(snapshot.data().postId).get();

    if (userWhoComments.data().id === post.data().userId) {
      console.log('Comment notification not sent because it is the same user of the post');
    } else {
      const payload = {
        notification: {
          title: `${userWhoComments.data().username} has commented on your post`,
        }
      };

      const tokenToNotificate = await admin.firestore().collection('Tokens').doc(post.data().userId).get();
      await admin.messaging().sendToDevice(tokenToNotificate.data().token, payload);
      console.log(`Comment Notifications have been sent from: ${userWhoComments.data().username} to: ${tokenToNotificate.id}`);
    }
  });

  exports.sendMessageNotification = functions.firestore.document('Messages/{id}')
  .onCreate(async (snapshot) => {
    const userSender = await admin.firestore().collection('Users').doc(snapshot.data().userIdSender).get();
    const userReciver = await admin.firestore().collection('Users').doc(snapshot.data().userIdReciver).get();

      const payload = {
        notification: {
          title: `${userSender.data().username} has sended a message`,
          body: `${snapshot.data().messageText}`,
        }
      };

      const tokenToNotificate = await admin.firestore().collection('Tokens').doc(userReciver.data().id).get();
      await admin.messaging().sendToDevice(tokenToNotificate.data().token, payload);
      console.log(`Message Notifications have been sent from: ${userSender.data().username} to: ${userReciver.data().username}`);
    
  });
