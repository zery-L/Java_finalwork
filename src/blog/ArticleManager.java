package blog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ArticleManager {
    private Map<String, Article> articles = new HashMap<>();
    private static final String ARTICLE_FOLDER_PATH = "articles";
    private static final String ARTICLE_BY_EMAIL_FOLDER_PATH = "articles_by_email";
    private static final String ARTICLE_LIKES_FOLDER_PATH = "article_likes";

    // 构造函数，调用读取方法
    public ArticleManager() {
        readArticlesFromFolder();
        loadArticleLikes();
    }

    // 通用文件读取方法，根据给定路径和文件名读取文章内容构建 Article 对象
    private Article readArticleFromFile(String folderPath, String fileName) {
        try {
            FileInputStream fis = new FileInputStream(folderPath + File.separator + fileName);
            Scanner fileScanner = new Scanner(fis);
            if (fileScanner.hasNextLine()) {
                String title = fileScanner.nextLine();
                StringBuilder contentBuilder = new StringBuilder();
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    if ("END".equals(line)) {
                        break;
                    }
                    contentBuilder.append(line).append("\n");
                }
                if (fileScanner.hasNextLine()) {
                    String authorUsername = fileScanner.nextLine();
                    Article article = new Article(title, contentBuilder.toString(), authorUsername);
                    fileScanner.close();
                    fis.close();
                    return article;
                }
            }
            fileScanner.close();
            fis.close();
            return null;
        } catch (IOException e) {
            System.out.println("读取文章文件时出错：" + e.getMessage());
            return null;
        }
    }

    // 从文件夹读取文章并放入 articles 列表的方法，使用通用读取方法
    private void readArticlesFromFolder() {
        File articleFolder = new File(ARTICLE_FOLDER_PATH);
        if (articleFolder.exists() && articleFolder.isDirectory()) {
            File[] articleFiles = articleFolder.listFiles();
            if (articleFiles != null) {
                for (File articleFile : articleFiles) {
                    Article article = readArticleFromFile(ARTICLE_FOLDER_PATH, articleFile.getName());
                    if (article != null) {
                        articles.put(article.getTitle(), article);
                    }
                }
            }
        }
    }

    // 整合后的更新文章文件方法，可根据标志位更新不同部分
    private void updateArticleFile(Article article, int updateType) {
        try {
            FileOutputStream fos = new FileOutputStream(
                    ARTICLE_FOLDER_PATH + File.separator + article.getTitle() + ".txt");
            if (updateType == 1) {
                // 更新文章内容和作者名
                fos.write(article.getTitle().getBytes());
                fos.write("\n".getBytes());
                fos.write(article.getContent().getBytes());
                fos.write("END".getBytes());
                fos.write("\n".getBytes());
                fos.write(article.getAuthorUsername().getBytes());
                fos.write("\n".getBytes());
            } else if (updateType == 2) {
                // 仅更新文章内容
                fos.write(article.getTitle().getBytes());
                fos.write("\n".getBytes());
                fos.write(article.getContent().getBytes());
                fos.write("END".getBytes());
                fos.write("\n".getBytes());
            }
            fos.close();
        } catch (IOException e) {
            System.out.println("更新文章文件时出错：" + e.getMessage());
        }
    }

    // 更新文件中的文章内容和作者名的方法，调用整合后的更新方法
    private void updateAuthorNameInFile(Article article, String originalAuthor) {
        article.setAuthorUsername(originalAuthor);
        updateArticleFile(article, 1);
    }

    // 更新文件中的文章内容的方法，调用整合后的更新方法
    private void updateArticleInFile(Article article) {
        updateArticleFile(article, 2);
    }

    // 保存文章到文件的方法，根据新的数据结构调整
    private void saveArticleToFile(Article article) {
        try {
            // 创建文章保存的文件夹（如果不存在）
            File articleFolder = new File(ARTICLE_FOLDER_PATH);
            if (!articleFolder.exists()) {
                articleFolder.mkdirs();
            }
            // 以文章标题为文件名创建文件
            File articleFile = new File(ARTICLE_FOLDER_PATH + File.separator + article.getTitle() + ".txt");
            FileOutputStream fos = new FileOutputStream(articleFile);
            // 写入文章信息
            fos.write(article.getTitle().getBytes());
            fos.write("\n".getBytes());
            fos.write(article.getContent().getBytes());
            fos.write("END".getBytes());
            fos.write("\n".getBytes());
            fos.write(article.getAuthorUsername().getBytes());
            fos.write("\n".getBytes());
            fos.close();
        } catch (IOException e) {
            System.out.println("保存文章到文件时出错：" + e.getMessage());
        }
    }

    // 将文章标题写入对应邮箱的文件中的方法
    private void saveArticleTitleToEmailFile(Article article) {
        try {
            // 获取当前登录用户的邮箱
            String email = JavaBlogSystem.currentLoggedInUser.getEmail();
            // 以邮箱为文件名创建文件
            File emailFile = new File(ARTICLE_BY_EMAIL_FOLDER_PATH + File.separator + email + ".txt");
            FileOutputStream fos = new FileOutputStream(emailFile);
            // 写入文章标题，换行符分隔
            fos.write(article.getTitle().getBytes());
            fos.write("\n".getBytes());
            fos.close();
        } catch (IOException e) {
            System.out.println("保存文章标题到邮箱文件时出错：" + e.getMessage());
        }
    }

    // 处理用户对文章的喜欢操作的方法
    public void handleArticleLike(Scanner scanner, Article article) {
        System.out.println("请输入 1 表示喜欢，0 表示不喜欢：");
        int like = scanner.nextInt();
        if (like == 1) {
            article.setLikedCount(article.getLikedCount() + 1);
            article.setLiked(true);
            saveArticleLikesToFile(article);
        } else {
            article.setLiked(false);
            saveArticleLikesToFile(article);
        }
    }

    // 从文件读取文章的喜欢数据并初始化文章属性的方法
    private void loadArticleLikes() {
        File likesFolder = new File(ARTICLE_LIKES_FOLDER_PATH);
        if (likesFolder.exists() && likesFolder.isDirectory()) {
            File[] likeFiles = likesFolder.listFiles();
            if (likeFiles != null) {
                for (File likeFile : likeFiles) {
                    try {
                        FileInputStream fis = new FileInputStream(likeFile);
                        Scanner fileScanner = new Scanner(fis);
                        // 获取文章标题
                        String title = likeFile.getName().substring(0, likeFile.getName().length() - 10);
                        Article article = articles.get(title);
                        if (article != null) {
                            // 读取喜欢数量
                            if (fileScanner.hasNextInt()) {
                                article.setLikedCount(fileScanner.nextInt());
                            }
                            // 读取喜欢状态
                            if (fileScanner.hasNextBoolean()) {
                                article.setLiked(fileScanner.nextBoolean());
                            }
                        }
                        fileScanner.close();
                        fis.close();
                    } catch (IOException e) {
                        System.out.println("读取喜欢数据文件时出错：" + e.getMessage());
                    }
                }
            }
        } else {

            likesFolder.mkdirs();
        }
    }

    // 文章管理流程方法
    public void articleManagement(Scanner scanner, String username) {
        boolean exitArticleManagement = false;
        while (!exitArticleManagement) {
            printArticleManagementMenu();
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    publishArticle(scanner, username);
                    break;
                case 2:
                    viewArticles(username);
                    break;
                case 3:
                    editArticle(scanner, username);
                    break;
                case 4:
                    deleteArticle(scanner, username);
                    break;
                case 5:
                    exitArticleManagement = true;
                    break;
                default:
                    System.out.println("无效的选择，请重新输入。");
            }
        }
    }

    // 发布文章方法
    public void publishArticle(Scanner scanner, String username) {
        System.out.print("请输入文章标题：");
        String title = scanner.next();
        scanner.nextLine();
        StringBuilder contentBuilder = new StringBuilder();
        System.out.println("请输入文章内容，输入 END 结束：");
        String line;
        while (!(line = scanner.nextLine()).equals("END")) {
            contentBuilder.append(line).append("\n");
        }
        contentBuilder.append("END");
        Article newArticle = new Article(title, contentBuilder.toString(),
                JavaBlogSystem.currentLoggedInUser.getUsername());
        articles.put(newArticle.getTitle(), newArticle);
        // 将文章保存到文件
        saveArticleToFile(newArticle);
        // 将文章标题写入对应邮箱的文件中
        saveArticleTitleToEmailFile(newArticle);
        System.out.println("文章发布成功！");
    }

    // 查看文章列表方法
    public void viewArticles(String username) {
        System.out.println("您发布的的文章列表：");

        articles.clear();
        File articleFolder = new File(ARTICLE_FOLDER_PATH);
        if (articleFolder.exists() && articleFolder.isDirectory()) {
            File[] articleFiles = articleFolder.listFiles();
            if (articleFiles != null) {
                for (File articleFile : articleFiles) {
                    Article article = readArticleFromFile(ARTICLE_FOLDER_PATH, articleFile.getName());
                    if (article != null && article.getAuthorUsername().equals(username)) {
                        articles.put(article.getTitle(), article);
                        System.out.println("标题：" + article.getTitle());
                    }
                }
            }
        }
    }

    // 编辑文章方法
    public void editArticle(Scanner scanner, String username) {
        System.out.println("请选择要编辑的文章：");
        viewArticles(username);
        System.out.print("请输入文章标题：");
        String title = scanner.next();
        scanner.nextLine();
        Article article = articles.get(title);
        if (article != null && article.getAuthorUsername().equals(username)) {
            StringBuilder newContentBuilder = new StringBuilder();
            System.out.println("请输入新的文章内容，输入 END 结束：");
            String line;
            while (!(line = scanner.nextLine()).equals("END")) {
                newContentBuilder.append(line).append("\n");
            }
            // 获取文章原始作者名
            String originalAuthor = article.getAuthorUsername();
            // 更新文章内容
            article.setContent(newContentBuilder.toString());
            // 更新文件中的文章内容
            updateArticleInFile(article);
            updateAuthorNameInFile(article, originalAuthor);
            System.out.println("文章编辑成功！");
            return;
        }
        System.out.println("未找到指定文章，请重新输入。");
    }

    // 删除文章方法
    public void deleteArticle(Scanner scanner, String username) {
        System.out.println("请选择要删除的文章：");
        viewArticles(username);
        System.out.print("请输入文章标题：");
        String title = scanner.next();
        Article article = articles.get(title);
        if (article != null && article.getAuthorUsername().equals(username)) {
            articles.remove(title);
            deleteArticleFile(article);

            System.out.println("文章删除成功！");
            return;
        }
        System.out.println("未找到指定文章，请重新输入。");
    }

    // 从文件中删除文章的方法
    private void deleteArticleFile(Article article) {
        try {
            // 以文章标题为文件名找到对应的文件
            File articleFile = new File(ARTICLE_FOLDER_PATH + File.separator + article.getTitle() + ".txt");
            // 先检查文件是否存在
            if (!articleFile.exists()) {
                System.out.println("要删除的文章文件不存在。");
                return;
            }
            articleFile.delete();
        } catch (SecurityException e) {
            System.out.println("删除文章文件时出错，可能是权限不足：" + e.getMessage());
        }
    }

    // 将文章的喜欢数据保存到文件的方法
    private void saveArticleLikesToFile(Article article) {
        File likeFile = new File(ARTICLE_LIKES_FOLDER_PATH + File.separator + article.getTitle() + "_likes.txt");
        // 检查文件所在的文件夹是否存在，如果不存在则创建
        File parentFolder = likeFile.getParentFile();
        if (!parentFolder.exists()) {
            parentFolder.mkdirs();
        }
        try {
            FileOutputStream fos = new FileOutputStream(likeFile);
            // 写入喜欢数量
            fos.write(String.valueOf(article.getLikedCount()).getBytes());
            fos.write("\n".getBytes());
            // 写入喜欢状态，将 boolean 值转换为字符串再转换为字节数组
            fos.write(String.valueOf(article.getLiked()).getBytes());
            fos.write("\n".getBytes());
            fos.close();
        } catch (IOException e) {
            System.out.println("保存文章喜欢数据到文件时出错：" + e.getMessage());
        }
    }

    // 打印文章管理菜单
    private static void printArticleManagementMenu() {
        System.out.println("------------------------------------------------");
        System.out.println("文章管理界面");
        System.out.println("------------------------------------------------");
        System.out.println("1. 发布文章");
        System.out.println("2. 查看文章列表");
        System.out.println("3. 编辑文章（需选择文章）");
        System.out.println("4. 删除文章（需选择文章）");
        System.out.println("5. 返回主界面");
        System.out.println("请选择您的操作（输入数字）：");
    }

    // 获取文章列表的方法，对外提供统一获取方式
    public List<Article> getArticles() {
        return new ArrayList<>(articles.values());
    }
}