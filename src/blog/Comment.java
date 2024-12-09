package blog;

import java.io.Serializable;

// 评论类，实现Serializable接口以便能进行对象序列化（用于后续可能的保存操作）
public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String commenter; // 评论者用户名，如果是未登录用户则可以设置为"匿名"之类的标识
    private String content;
    private String articleTitle;
    private Comment parentComment; // 用于指向父评论，形成楼中楼效果，若为null则表示是文章的一级评论

    public Comment(String commenter, String content, String articleTitle) {
        this.commenter = commenter;
        this.content = content;
        this.articleTitle = articleTitle;
        this.parentComment = null;
    }

    public Comment(String commenter, String content, String articleTitle, Comment parentComment) {
        this.commenter = commenter;
        this.content = content;
        this.articleTitle = articleTitle;
        this.parentComment = parentComment;
    }

    public String getCommenter() {
        return commenter;
    }

    public String getContent() {
        return content;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public Comment getParentComment() {
        return parentComment;
    }

    public void setCommenter(String newCommenter) {
        this.commenter = newCommenter;
    }

    public void setContent(String newContent) {
        this.content = newContent;
    }

    public void setArticleTitle(String newArticleTitle) {
        this.articleTitle = newArticleTitle;
    }

    public void setParentComment(Comment newParentComment) {
        this.parentComment = newParentComment;
    }
}