package blog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

// 用户管理类
public class UserManager {
    private ArrayList<User> users = new ArrayList<>();
    private static final String USER_FOLDER_PATH = "users"; // 保存用户信息的文件夹路径
    private static final String ARTICLE_BY_EMAIL_FOLDER_PATH = "articles_by_email"; // 保存以邮箱号为名的txt文件的文件夹路径

    // 用户注册方法
    public void userRegister(Scanner scanner) {
        System.out.print("请输入用户名：");
        String username = scanner.next();
        System.out.print("请输入密码：");
        String password = scanner.next();
        System.out.print("请输入邮箱：");
        String email = scanner.next();

        if (isUsernameExists(username)) {
            System.out.println("请重新选择一个用户名。");
            return;
        }

        User newUser = new User(username, password, email);
        users.add(newUser);

        // 将新注册用户信息保存到文件
        saveUserToFile(newUser);

        // 创建按邮箱存储文章名的文件夹（如果不存在）
        createArticleByEmailFolder();

        System.out.println("注册成功！");
    }

 // 用户登录方法（修改后从文件读取用户信息用于验证）
    public void userLogin(Scanner scanner) {
        System.out.print("请输入用户名：");
        String username = scanner.next();
        System.out.print("请输入密码：");
        String password = scanner.next();

        File userFolder = new File(USER_FOLDER_PATH);
        if (userFolder.exists() && userFolder.isDirectory()) {
            File[] userFiles = userFolder.listFiles();
            if (userFiles!= null) {
                for (File userFile : userFiles) {
                    try {
                        FileInputStream fis = new FileInputStream(userFile);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        User user = (User) ois.readObject();
                        ois.close();
                        fis.close();
                        if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                            System.out.println("登录成功！");
                            // 将当前登录用户对象赋值给JavaBlogSystem.currentLoggedInUser
                            JavaBlogSystem.currentLoggedInUser = user;
                            return;
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("读取用户文件时出错：" + e.getMessage());
                    }
                }
            }
        }

        System.out.println("用户名或密码错误，请重新输入。");
    }

    // 检查用户名是否已存在
    private boolean isUsernameExists(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    // 获取用户列表的方法
    public ArrayList<User> getUsers() {
        return users;
    }

    // 将用户信息保存到文件的方法
    private void saveUserToFile(User user) {
        try {
            // 创建用户信息保存的文件夹（如果不存在）
            File userFolder = new File("users");
            if (!userFolder.exists()) {
                userFolder.mkdirs();
            }

            // 以用户名作为文件名创建文件
            File userFile = new File(USER_FOLDER_PATH + File.separator + user.getUsername() + ".txt");
            FileOutputStream fos = new FileOutputStream(userFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // 将用户对象序列化并写入文件
            oos.writeObject(user);

            oos.close();
            fos.close();
        } catch (IOException e) {
            System.out.println("保存用户信息到文件时出错：" + e.getMessage());
        }
    }

    // 创建按邮箱存储文章名的文件夹（如果不存在）
    private void createArticleByEmailFolder() {
        File articleByEmailFolder = new File(ARTICLE_BY_EMAIL_FOLDER_PATH);
        if (!articleByEmailFolder.exists()) {
            articleByEmailFolder.mkdirs();
        }
    }
}