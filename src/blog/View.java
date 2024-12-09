package blog;

import java.util.ArrayList;
import java.util.Scanner;

public class View {
    private ArticleManager articleManager;
    private CommentManager commentManager;

    public View(ArticleManager articleManager) {
        this.articleManager = articleManager;
        this.commentManager = new CommentManager();
        commentManager.initialize();
    }

    // 查看博客功能的实现方法
    public void viewBlogs(Scanner scanner) {
        // 获取文章列表的调整
        ArrayList<Article> allArticles = new ArrayList<>(articleManager.getArticles());
        for (Article article : allArticles) {
            System.out.println("文章标题：" + article.getTitle() + " " + (article.getLikedCount() > 0? article.getLikedCount() + " LIKED" : "0LIKED"));
        }
        System.out.println("请输入文章标题查看内容（输入 q 退出）：");
        String title = scanner.next();
        if (title.equals("q")) {
            return;
        }
        for (Article article : allArticles) {
            if (article.getTitle().equals(title)) {
                System.out.println("文章标题：" + article.getTitle());
                System.out.println("文章内容：\n" + article.getContent());
                // 调用 ArticleManager 的 handleArticleLike 方法处理喜欢操作
                articleManager.handleArticleLike(scanner, article);
                // 显示文章的评论
                displayComments(scanner, article.getTitle());
                // 重新获取文章列表以更新显示
                allArticles = new ArrayList<>(articleManager.getArticles());
                for (Article updatedArticle : allArticles) {
                    if (updatedArticle.getTitle().equals(article.getTitle())) {
                        System.out.println("文章标题：" + updatedArticle.getTitle() + " " + (updatedArticle.getLikedCount() > 0? updatedArticle.getLikedCount() + " LIKED" : "0LIKED"));
                        break;
                    }
                }
                return;
            }
        }
        System.out.println("未找到指定文章，请重新输入。");
    }

    // 显示文章评论的方法
    private void displayComments(Scanner scanner, String articleTitle) {
        boolean exitCommentSection = false;
        while (!exitCommentSection) {
            System.out.println("文章的评论：");
            ArrayList<Comment> comments = (ArrayList<Comment>) commentManager.getArticleCommentsMap().get(articleTitle);
            if (comments == null || comments.isEmpty()) {
                System.out.println("该文章暂无评论。");
            } else {
                for (Comment comment : comments) {
                    commentManager.printComment(comment);
                }
            }
            System.out.println("1. 发表评论（无需登录）");
            if (JavaBlogSystem.currentLoggedInUser!= null) {
                System.out.println("2. 查看我的文章评论");
                System.out.println("3. 回复评论");
            }
            System.out.println("q. 返回查看博客界面");
            System.out.println("请选择操作（输入相应字符）：");
            String choice = scanner.next();
            scanner.nextLine(); // 消耗换行符
            switch (choice) {
                case "1":
                    commentManager.commentOnArticle(scanner, articleTitle);
                    break;
                case "2":
                    if (JavaBlogSystem.currentLoggedInUser!= null) {
                        commentManager.viewCommentsForOwnArticle(scanner, JavaBlogSystem.currentLoggedInUser.getUsername());
                    } else {
                        System.out.println("请先登录再查看自己文章的评论。");
                    }
                    break;
                case "3":
                    if (JavaBlogSystem.currentLoggedInUser!= null) {
                        commentManager.replyToComment(scanner, JavaBlogSystem.currentLoggedInUser.getUsername(), articleTitle);
                    } else {
                        System.out.println("请先登录再回复评论。");
                    }
                    break;
                case "q":
                    exitCommentSection = true;
                    break;
                default:
                    System.out.println("无效的选择，请重新输入。");
            }
        }
    }
}