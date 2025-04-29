DROP DATABASE IF EXISTS `comment-service`;
CREATE DATABASE IF NOT EXISTS `comment-service`;
USE `comment-service`;

DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `pid` bigint NOT NULL COMMENT '被回复的帖子ID',
  `from_uid` bigint NOT NULL COMMENT '回复者ID',
  `to_uid` bigint NULL DEFAULT NULL COMMENT '回复谁',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '回复的内容',
  `parent_id` int NULL DEFAULT NULL COMMENT '子级回复的父级回复ID，根级评论为0',
  `reply_id` int NULL DEFAULT NULL COMMENT '楼中楼中回复目标楼层的ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `comments_pid`(`pid` ASC) USING BTREE,
  INDEX `comments_uid`(`from_uid` ASC) USING BTREE
  -- 其他微服务
  -- ,CONSTRAINT `comments_pid` FOREIGN KEY (`pid`) REFERENCES `post` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  -- CONSTRAINT `comments_uid` FOREIGN KEY (`from_uid`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

INSERT INTO `comment` VALUES (1, 1610095136614686721, 1591314523924168706, NULL, '<p>一道非常基本的杂项编码题，提示是有关base编码（除了base64，base32，base16，还有什么呢？），当时被提示所误导，想方设法找base8编码，没有找到。正确思路应该是查找关键词:base编码，emoji（题目本身），联立搜索找到能编码为emoji的base100，以下是github中用rust写的 <a href=\"https://github.com/AdamNiederer/base100\">base100</a> 编/解码仓库，可以按照 readme.md 提示拿到flag:</p>\n<blockquote>\n<p>欢迎来到躺平杯！这是flag1: flag{Happy_NEW_YEAR_2023}</p>\n</blockquote>\n<p>感兴趣可以试着读一读main.rc文件，我就不献丑了</p>\n', 0, 0, '2023-01-23 09:36:37');
INSERT INTO `comment` VALUES (2, 1610095136614686721, 1591315551016943618, 1591314523924168706, '具体的事给我们具体的目标，十分明确，于是更容易有干劲。从具体事情中得到反馈，给我们更直接的激励，和易于感知的意义感。如果愿意，完全可以享受过程。', 1, 1, '2023-01-23 09:37:01');
INSERT INTO `comment` VALUES (3, 1610095136614686721, 1591314523924168706, 1591315551016943618, '遇到困难，也是眼前的一个小困难。心里想的不是距离目标还有多远，而是刚刚取得了哪项小成就，以及正在攻克的下一个小目标。', 1, 2, '2023-01-23 09:38:25');
INSERT INTO `comment` VALUES (4, 1610095136614686721, 1591317256513216513, 1591314523924168706, '过程中是一件件具体的事。在做具体事的时候，我们更加得心应手。具体的事给我们具体的目标，十分明确，于是更容易有干劲。', 1, 1, '2023-01-23 09:40:01');
INSERT INTO `comment` VALUES (5, 1610095136614686721, 1591317667206881281, NULL, '首先，人生以及任何事的意义，都在过程而不在结果。认识到这一点是一个缓慢的过程，目前我还处在起初觉察的阶段，未来需要漫长的岁月去知行合一。 其次，就算目的仅仅是达成结果，能使上劲的也只有通往结果的过程。所以即便以结果为导向，唯一能做的还是过程中的每一步。', 0, 0, '2023-01-23 09:37:23');
INSERT INTO `comment` VALUES (6, 1610095136614686721, 1593245065385066497, 1591317667206881281, '回复第二条根级评论，这是第三条二级回复', 5, 5, '2023-01-23 09:38:53');
INSERT INTO `comment` VALUES (7, 1610095136614686721, 1596342424588832769, 1593245065385066497, '回复第三条二级回复，这是第二条三级回复', 5, 6, '2023-01-23 09:42:21');
INSERT INTO `comment` VALUES (8, 1610095136614686721, 1593245065385066497, 1596342424588832769, '回复第二条三级回复，这是第三条三级回复', 5, 7, '2023-01-23 09:45:24');
INSERT INTO `comment` VALUES (9, 1610095136614686721, 1596342424588832769, 1591317667206881281, '测试', 5, 5, '2023-01-23 09:43:24');
INSERT INTO `comment` VALUES (23, 1610095136614686721, 1591314523924168706, NULL, '首先，人生以及任何事的意义，都在过程而不在结果。认识到这一点是一个缓慢的过程，目前我还处在起初觉察', 0, 0, '2023-02-01 20:05:24');
INSERT INTO `comment` VALUES (24, 1610095136614686721, 1591314523924168706, NULL, '&lt;p&gt;一道非常基本的杂项编码题，提示是有关base编码（除了base64，base32，base16，还有什么呢？），当时被提示所误导，想方设法找base8编码，没有找到。&lt;/p&gt;\n', 0, 0, '2023-02-02 08:28:40');
INSERT INTO `comment` VALUES (25, 1610095136614686721, 1591314523924168706, NULL, '&lt;p&gt;测试&lt;/p&gt;\n', 0, 0, '2023-02-03 11:04:34');
INSERT INTO `comment` VALUES (26, 1610095136614686721, 1591314523924168706, 1591317256513216513, '测试回复', 1, 4, '2023-02-03 14:39:02');
INSERT INTO `comment` VALUES (27, 1610095136614686721, 1591314523924168706, 1591314523924168706, '继续测试', 24, 24, '2023-02-03 14:55:03');
INSERT INTO `comment` VALUES (28, 1610095136614686721, 1591314523924168706, 1591314523924168706, '一道非常基本的杂项编码题，提示是有关base编码（除了base64，base32，base16，还有什么呢？），当时被提示所误导，想方设法找base8编码，没有找到。', 1, 1, '2023-02-03 21:38:13');
INSERT INTO `comment` VALUES (29, 1610095136614686721, 1591314523924168706, 1591314523924168706, '一道非常基本的杂项编码题，提示是有关base编码（除了base64，base32，base16，还有什么呢？），当时被提示所误导，想方设法找base8编码，没有找到。', 1, 28, '2023-02-03 21:38:22');
INSERT INTO `comment` VALUES (30, 1610095136614686721, 1591314523924168706, 1591314523924168706, '一道非常基本的杂项编码题，提示是有关base编码（除了base64，base32，base16，还有什么呢？），当时被提示所误导，想方设法找base8编码，没有找到。', 1, 29, '2023-02-03 21:38:39');
INSERT INTO `comment` VALUES (31, 1610095136614686721, 1591314523924168706, 1591314523924168706, '测试测试测试', 24, 27, '2023-02-04 19:39:37');
INSERT INTO `comment` VALUES (32, 1610095136614686721, 1591314523924168706, 1591314523924168706, '测试测试测试', 24, 24, '2023-02-04 19:39:55');
INSERT INTO `comment` VALUES (33, 1610095136614686721, 1591314523924168706, 1591314523924168706, '测试', 25, 25, '2023-02-04 19:41:56');
INSERT INTO `comment` VALUES (34, 1610095160929067010, 1591314523924168706, NULL, '<p>既然如此，我们还需要学习提示技巧吗？我的回答是，学！因为错过就太可惜了。人工智能在目前这个阶段停留不会太久，我们有幸在它还比较生涩的时候摸索着使用，这种体验以后可能都不会有了。</p>\n<p>说到这里终于要点题了，AI 时代生存指南：</p>\n<blockquote>\n<p>第二条：乐于使用 AI</p>\n</blockquote>\n<p>注意是乐于，而不是善于。一开始不会用没关系，带上好奇心去尝试，相信自己会成为 AI 时代的受益者。只要多用，变着花样的用，很快就能找到使用 AI 的感觉。是感觉，而不是技巧。</p>\n', 0, 0, '2023-02-13 21:08:20');
INSERT INTO `comment` VALUES (35, 1610095160929067010, 1591314523924168706, 1591314523924168706, '测试回复', 34, 34, '2023-02-13 21:48:42');
INSERT INTO `comment` VALUES (36, 1609896692251926529, 1591314015905873922, NULL, '&lt;p&gt;现在其实不缺信息，只是缺乏对信息的有效筛选机制，于是我干起了人工筛选信息的事。&lt;/p&gt;\n&lt;p&gt;互联网 Newsletter 这种形式，多年前就有了，信息送达不够及时，后来邮件也成了一种“营销”形式，让许多人提起 Email 就想到垃圾邮件。&lt;/p&gt;\n&lt;p&gt;以及现在很多人很难静下心来写一封“邮件”。&lt;/p&gt;\n&lt;p&gt;于是，我做了一个“技术简报”，每周一封，让邮件变为你的信息收件箱。&lt;/p&gt;\n&lt;p&gt;每周为开发者轻简读：技术创业、酷产品、新技术、编程语言、开发工具、DevOps…等内容。&lt;/p&gt;\n', 0, 0, '2023-02-24 15:43:05');
INSERT INTO `comment` VALUES (37, 1609896344619622401, 1591314015905873922, NULL, '&lt;p&gt;仓鼠的特性是喜欢囤积东西（粮食），而数字仓鼠指的是喜欢囤积网络资源的人。&lt;/p&gt;\n&lt;p&gt;以前我对于囤积网络资源不上心，那时候很多资源，只要会搜索，多少能搜索到，不过从前年开始我就开始刻意囤积，做好分门别类，因为我隐隐有种感觉，未来这些东西在网络上会变少，很多东西即便没有消失也会淹没在互联网的角落中。&lt;/p&gt;\n&lt;p&gt;拿一个很经典的动画来说，《恶魔人》的旧版 TV 动画，当初 B 站有旧版 TV 动画全集，个人上传的视频，后续不知何种原因视频做下架处理，几个月后我想下载这个旧版动画的资源时，只在网络上找到了一个 bt ，然而没有人做种根本下不动😂。&lt;/p&gt;\n&lt;p&gt;还有就是一些网络小说，有些被封禁的网络小说虽然盗版网站有，但因为这类小说早已被封禁多年，基本上是靠口口相传，导致不少好作品都容易消声灭迹，如果未来盗版小说网站大幅度减少后，这类网络小说会不会就隐匿在互联网的哪个犄角旮旯里了。&lt;/p&gt;\n&lt;p&gt;出于这些考量，我开始囤积一些旧资源和冷门资源，但囤积到一定数量后我就开始担心，万一未来我硬盘坏了怎么办？如果我忘记了我有这个资源？我突然醒悟过来，真正的囤法应该是把资源分享出去，只要拥有的人多了，到时候找资源也容易了，这比存网盘、存硬盘、刻光碟还靠谱。&lt;/p&gt;\n', 0, 0, '2023-02-24 15:49:03');
INSERT INTO `comment` VALUES (38, 1609896344619622401, 1591314015905873922, NULL, '&lt;p&gt;以前叫松鼠党，现在叫数字仓鼠了？&lt;/p&gt;\n', 0, 0, '2023-02-24 15:54:13');
INSERT INTO `comment` VALUES (39, 1628742470884638721, 1591314015905873922, NULL, '&lt;p&gt;I have gradle multi module project where one module has BDD serenity api tests&lt;/p&gt;\n&lt;p&gt;Structure&lt;/p&gt;\n&lt;pre&gt;&lt;code&gt;-module1\n - bddTests\n - serenity.properties\n-module2\n&lt;/code&gt;&lt;/pre&gt;\n&lt;p&gt;I have serenity.properties file inside the module where I got tests with below entry&lt;/p&gt;\n&lt;pre&gt;&lt;code&gt;serenity.outputDirectory=../target/site/serenity\n&lt;/code&gt;&lt;/pre&gt;\n&lt;p&gt;But after succes test run I can see empty folder created in the root i.e. &lt;code&gt;target/site/serenity&lt;/code&gt;&lt;/p&gt;\n&lt;p&gt;I tried to add another serenity.properties file with below, No luck though&lt;/p&gt;\n&lt;pre&gt;&lt;code&gt;serenity.outputDirectory=target/site/serenity\n&lt;/code&gt;&lt;/pre&gt;\n&lt;p&gt;I have single gradle module projects where serenity reports generate perfectly at the root.&lt;/p&gt;\n', 0, 0, '2023-02-24 16:15:04');
INSERT INTO `comment` VALUES (40, 1610095160929067010, 1591314015905873922, 1591314523924168706, '111', 34, 35, '2023-03-01 16:59:19');
INSERT INTO `comment` VALUES (43, 1628742470884638721, 1591314523924168706, NULL, '&lt;p&gt;测试 &lt;code&gt;last_comment_time&lt;/code&gt;&lt;/p&gt;\n', 0, 0, '2023-03-05 10:51:02');
INSERT INTO `comment` VALUES (44, 1609895924132253698, 1591315551016943618, NULL, '&lt;p&gt;测试 &lt;code&gt;last_comment_time&lt;/code&gt;&lt;/p&gt;\n', 0, 0, '2023-03-05 10:55:01');
INSERT INTO `comment` VALUES (45, 1609896692251926529, 1591315551016943618, NULL, '&lt;p&gt;测试 &lt;code&gt;last_comment_time&lt;/code&gt;&lt;/p&gt;\n', 0, 0, '2023-03-05 10:55:13');
INSERT INTO `comment` VALUES (46, 1610094821865725954, 1591315551016943618, NULL, '&lt;p&gt;测试 &lt;code&gt;last_comment_time&lt;/code&gt;&lt;/p&gt;\n', 0, 0, '2023-03-05 10:55:28');
INSERT INTO `comment` VALUES (47, 1610094836965220354, 1591315551016943618, NULL, '&lt;p&gt;测试 &lt;code&gt;last_comment_time&lt;/code&gt;&lt;/p&gt;\n', 0, 0, '2023-03-05 10:55:47');
INSERT INTO `comment` VALUES (48, 1610094860814032897, 1591315551016943618, NULL, '&lt;p&gt;测试 &lt;code&gt;last_comment_time&lt;/code&gt;&lt;/p&gt;\n', 0, 0, '2023-03-05 10:56:14');
INSERT INTO `comment` VALUES (49, 1610094906020241410, 1591315551016943618, NULL, '&lt;p&gt;测试 &lt;code&gt;last_comment_time&lt;/code&gt;&lt;/p&gt;\n', 0, 0, '2023-03-05 10:56:24');
INSERT INTO `comment` VALUES (50, 1610094931395780610, 1591315551016943618, NULL, '&lt;p&gt;测试 &lt;code&gt;last_comment_time&lt;/code&gt;&lt;/p&gt;\n', 0, 0, '2023-03-05 10:56:37');
INSERT INTO `comment` VALUES (51, 1610094985737183234, 1591315551016943618, NULL, '&lt;p&gt;测试 &lt;code&gt;last_comment_time&lt;/code&gt;&lt;/p&gt;\n', 0, 0, '2023-03-05 10:56:45');
INSERT INTO `comment` VALUES (52, 1610095122886729730, 1591315551016943618, NULL, '&lt;p&gt;测试 &lt;code&gt;last_comment_time&lt;/code&gt;&lt;/p&gt;\n', 0, 0, '2023-03-05 10:56:53');
INSERT INTO `comment` VALUES (53, 1610095136614686721, 1591315551016943618, NULL, '&lt;p&gt;测试 &lt;code&gt;last_comment_time&lt;/code&gt;&lt;/p&gt;\n', 0, 0, '2023-03-05 10:57:02');
INSERT INTO `comment` VALUES (54, 1610095160929067010, 1591315551016943618, NULL, '&lt;p&gt;测试 &lt;code&gt;last_comment_time&lt;/code&gt;&lt;/p&gt;\n', 0, 0, '2023-03-05 10:57:10');
INSERT INTO `comment` VALUES (55, 1611007289072828418, 1591315551016943618, NULL, '&lt;p&gt;测试 &lt;code&gt;last_comment_time&lt;/code&gt;&lt;/p&gt;\n', 0, 0, '2023-03-05 10:57:21');
INSERT INTO `comment` VALUES (56, 1628742470884638721, 1591315551016943618, NULL, '&lt;p&gt;测试 &lt;code&gt;last_comment_time&lt;/code&gt;&lt;/p&gt;\n', 0, 0, '2023-03-05 10:57:32');
INSERT INTO `comment` VALUES (57, 1610094821865725954, 1591315551016943618, NULL, '&lt;p&gt;几经尝试，最终考虑将base64图片转位ObjectUrl再加载，好处是无需后端，纯前端即可兼容。移动端兼容性也非常不错。&lt;/p&gt;\n', 0, 0, '2023-03-05 11:41:12');
INSERT INTO `comment` VALUES (58, 1610095122886729730, 1591314015905873922, NULL, '&lt;p&gt;测试敏感词，俺们的****正常&lt;/p&gt;\n', 0, 0, '2023-04-09 15:47:48');
INSERT INTO `comment` VALUES (59, 1610095122886729730, 1591314015905873922, 1591314015905873922, '我们要实现的是****，做****接班人', 58, 58, '2023-04-09 15:48:51');
INSERT INTO `comment` VALUES (60, 1644999229460013057, 1591314015905873922, NULL, '&lt;p&gt;我们的*****是****，更是**，应该加以区分。&lt;/p&gt;\n', 0, 0, '2023-04-09 17:45:09');
INSERT INTO `comment` VALUES (61, 1644999229460013057, 1591314015905873922, 1591314015905873922, '**是完美的', 60, 60, '2023-04-09 17:46:27');
INSERT INTO `comment` VALUES (62, 1645000714746945537, 1591314015905873922, NULL, '&lt;p&gt;在****之争上，刻不容缓&lt;/p&gt;\n', 0, 0, '2023-04-14 20:32:41');
INSERT INTO `comment` VALUES (63, 1662295290566254593, 1591314015905873922, NULL, '&lt;p&gt;避免速度过快导致撞车&lt;/p&gt;\n', 0, 0, '2023-05-27 11:11:30');
INSERT INTO `comment` VALUES (64, 1662295290566254593, 1591314015905873922, 1591314015905873922, '尤其是下雨天', 63, 63, '2023-05-27 11:11:43');
INSERT INTO `comment` VALUES (65, 1610095160929067010, 1591314015905873922, NULL, '&lt;p&gt;特使&lt;/p&gt;\n', 0, 0, '2023-05-28 08:24:45');
INSERT INTO `comment` VALUES (66, 1610095160929067010, 1591314015905873922, 1591314015905873922, '认为团由网友', 65, 65, '2023-05-28 08:24:54');
INSERT INTO `comment` VALUES (67, 1662295290566254593, 1591314015905873922, NULL, '&lt;p&gt;大 ag **等东莞&lt;/p&gt;\n', 0, 0, '2023-05-28 08:27:27');
INSERT INTO `comment` VALUES (68, 1662295290566254593, 1591314015905873922, 1591314015905873922, 'asfasfsf ', 63, 64, '2023-05-28 08:28:31');
INSERT INTO `comment` VALUES (69, 1662295290566254593, 1591314015905873922, 1591314015905873922, 'test', 63, 68, '2024-03-10 14:16:01');

