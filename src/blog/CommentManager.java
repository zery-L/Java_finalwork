package blog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// 评论管理类
public class CommentManager {
    private static final String COMMENT_FOLDER_PATH = "comments"; // 保存评论的文件夹路径
    private Map<String, List<Comment>> articleCommentsMap = new HashMap<>(); // 以文章标题为键，对应评论列表为值的映射
    public Map<String, List<Comment>> getArticleCommentsMap() {
        return articleCommentsMap;
    }
    // 初始化方法，在程序启动时读取评论文件
    public void initialize() {
        File commentFolder = new File(COMMENT_FOLDER_PATH);
        if (commentFolder.exists() && commentFolder.isDirectory()) {
            File[] commentFiles = commentFolder.listFiles();
            if (commentFiles!= null) {
                for (File commentFile : commentFiles) {
                    try {
                        FileInputStream fis = new FileInputStream(commentFile);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        Comment comment = (Comment) ois.readObject();
                        ois.close();
                        fis.close();

                        addCommentToMap(comment);
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("读取评论文件时出错：" + e.getMessage());
                    }
                }
            }
        }
    }

    // 将评论添加到映射中的方法
    private void addCommentToMap(Comment comment) {
        String articleTitle = comment.getArticleTitle();
        if (!articleCommentsMap.containsKey(articleTitle)) {
            articleCommentsMap.put(articleTitle, new ArrayList<>());
        }
        articleCommentsMap.get(articleTitle).add(comment);
    }

    // 保存评论到文件的方法
    private void saveCommentToFile(Comment comment) {
        try {
            // 创建评论保存的文件夹（如果不存在）
            File commentFolder = new File(COMMENT_FOLDER_PATH);
            if (!commentFolder.exists()) {
                commentFolder.mkdirs();
            }

            // 以文章标题和评论者用户名组合作为文件名创建文件（可根据实际情况调整命名规则）
            String fileName = COMMENT_FOLDER_PATH + File.separator + comment.getArticleTitle() + "_" + comment.getCommenter() + ".txt";
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // 将评论对象序列化并写入文件
            oos.writeObject(comment);

            oos.close();
            fos.close();
        } catch (IOException e) {
            System.out.println("保存评论到文件时出错：" + e.getMessage());
        }
    }

    // 读者评论文章的方法
    public void commentOnArticle(Scanner scanner, String articleTitle) {
        System.out.print("请输入您的评论内容（无需登录，可匿名）：");
        String content = scanner.nextLine();
        String commenter = "匿名";
        if (JavaBlogSystem.currentLoggedInUser!= null) {
            commenter = JavaBlogSystem.currentLoggedInUser.getUsername();
        }

        Comment newComment = new Comment(commenter, content, articleTitle);
        addCommentToMap(newComment);
        saveCommentToFile(newComment);

        System.out.println("评论成功！可继续发表评论或按q返回查看博客界面。");
    }

    // 已登录用户查看自己文章评论的方法
    public void viewCommentsForOwnArticle(Scanner scanner, String username) {
        System.out.println("您发布的文章的评论如下：");

        for (Map.Entry<String, List<Comment>> entry : articleCommentsMap.entrySet()) {
            String articleTitle = entry.getKey();
            List<Comment> comments = entry.getValue();

            // 假设通过文章管理类能获取到文章作者，这里简单模拟检查文章作者是否为当前登录用户
            if (isArticleAuthor(username, articleTitle)) {
                System.out.println("文章标题：" + articleTitle);
                for (Comment comment : comments) {
                    printComment(comment);
                }
            }
        }
    }

    // 简单模拟检查文章作者是否为指定用户的方法，实际应用中需根据文章管理类获取准确信息
    private boolean isArticleAuthor(String username, String articleTitle) {
        // 这里假设通过某种方式获取到文章的真实作者，比如从ArticleManager获取文章对象再获取作者
        // 此处暂时简单返回true进行演示
        return true;
    }

    // 打印评论及其楼中楼回复的方法
    public void printComment(Comment comment) {
        System.out.println("\"" + comment.getContent() + "\" - " + comment.getCommenter());
        if (comment.getParentComment()!= null) {
            System.out.println("@" + comment.getParentComment().getCommenter() + " - \"" + comment.getContent() + "\" - " + comment.getCommenter());
            printComment(comment.getParentComment());
        }
    }

    // 已登录用户回复评论的方法
 // 已登录用户回复评论的方法
    public void replyToComment(Scanner scanner, String username, String articleTitle) {
        boolean exitReplySection = false;
        while (!exitReplySection) {
            System.out.println("请选择要回复的评论（输入评论内容的开头部分进行匹配）：");
            List<Comment> comments = articleCommentsMap.get(articleTitle);
            if (comments == null) {
                System.out.println("该文章暂无评论，无法回复。");
                break;
            }

            String searchContent = scanner.nextLine();
            Comment targetComment = null;
            for (Comment comment : comments) {
                if (comment.getContent().startsWith(searchContent)) {
                    targetComment = comment;
                    break;
                }
            }

            if (targetComment == null) {
                System.out.println("未找到指定评论，请重新输入。");
                break;
            }

            System.out.print("请输入您的副本内容：");
            String replyContent = scanner.nextLine();

            Comment replyComment = new Comment(username, replyContent, articleTitle, targetComment);
            addCommentToMap(replyComment);
            saveCommentToFile(replyComment);

            System.out.println("回复成功！可继续回复评论或按q返回查看博客界面。");

            System.out.println("q. 返回查看评论界面");
            System.out.println("请选择操作（输入相应字符）：");
            String choice = scanner.next();
            scanner.nextLine(); // 消耗换行符

            switch (choice) {
                case "q":
                    exitReplySection = true;
                    break;
                default:
                    System.out.println("无效的选择，请重新输入。");
            }
        }
    }
}