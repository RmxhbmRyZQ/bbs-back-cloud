package com.example.common.utils;

public class MessageQueueKeyUtils {

    // ===================== Post =====================
    public static final String POST_EXCHANGE = "post.es.exchange";

    public static final String POST_ROUTING_KEY_INSERT = "post.insert";
    public static final String POST_ROUTING_KEY_UPDATE_TITLE = "post.update.title";
    public static final String POST_ROUTING_KEY_UPDATE_CONTENT = "post.update.content";

    public static final String POST_QUEUE_INSERT = "post.es.insert.queue";
    public static final String POST_QUEUE_UPDATE_TITLE = "post.es.title.queue";
    public static final String POST_QUEUE_UPDATE_CONTENT = "post.es.content.queue";

    // ===================== User =====================
    public static final String USER_EXCHANGE = "user.es.exchange";

    public static final String USER_ROUTING_KEY_INSERT = "user.insert";
    public static final String USER_ROUTING_KEY_UPDATE_NICKNAME = "user.update.nickname";
    public static final String USER_ROUTING_KEY_UPDATE_AVATAR = "user.update.avatar";
    public static final String USER_ROUTING_KEY_UPDATE_EMAIL_VERIFIED = "user.update.emailVerified";
    public static final String USER_ROUTING_KEY_UPDATE_BANNED = "user.update.banned";
    public static final String USER_ROUTING_KEY_UPDATE_UNBANNED = "user.update.unbanned";

    public static final String USER_QUEUE_INSERT = "user.es.insert.queue";
    public static final String USER_QUEUE_UPDATE_NICKNAME = "user.es.nickname.queue";
    public static final String USER_QUEUE_UPDATE_AVATAR = "user.es.avatar.queue";
    public static final String USER_QUEUE_UPDATE_EMAIL_VERIFIED = "user.es.emailVerified.queue";
    public static final String USER_QUEUE_UPDATE_BANNED = "user.es.banned.queue";
    public static final String USER_QUEUE_UPDATE_UNBANNED = "user.es.unbanned.queue";
}
