package blog;

import java.util.Scanner;

// 主程序类，包含程序入口点及整体流程控制
public class JavaBlogSystem {
    private UserManager userManager;
    private ArticleManager articleManager;
    private View view;
    public static User currentLoggedInUser; // 用于保存当前登录用户的静态变量

    public JavaBlogSystem() {
        userManager = new UserManager();
        articleManager = new ArticleManager();
        view = new View(articleManager);
    }

    public static void main(String[] args) {
        JavaBlogSystem blogSystem = new JavaBlogSystem();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            blogSystem.printMainMenu();
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    blogSystem.userManager.userLogin(scanner);
                    break;
                case 2:
                    blogSystem.userManager.userRegister(scanner);
                    break;
                case 3:
                    if (JavaBlogSystem.currentLoggedInUser == null) {
                        System.out.println("请先登录再进行文章管理操作。");
                        break;
                    }
                    blogSystem.articleManager.articleManagement(scanner, JavaBlogSystem.currentLoggedInUser.getUsername());
                    break;
                case 4:
                    blogSystem.view.viewBlogs(scanner);
                    break;
                case 5:
                    exit = true;
                    break;
                default:
                    System.out.println("无效的选择，请重新输入。");
            }
        }

        scanner.close();
    }

    // 打印主菜单
    private void printMainMenu() {
        System.out.println("------------------------------------------------");
        System.out.println("Java简易博客系统");
        System.out.println("------------------------------------------------");
        System.out.println("1. 用户登录");
        System.out.println("2. 用户注册");
        System.out.println("3. 文章管理");
        System.out.println("4. 查看博客");
        System.out.println("5. 退出系统");
        System.out.println("请选择您的操作（输入数字）：");
    }
}