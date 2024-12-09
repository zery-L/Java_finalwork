package blog;

// 文章类
public class Article {
    private String title;
    private String content;
    private String authorUsername;
    private int likedCount;
    private boolean liked;

    public Article(String title, String content, String authorUsername) {
        this.title = title;
        this.content = content;
        this.authorUsername = authorUsername;
        this.likedCount = 0;
        this.liked = false;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
    public void setAuthorUsername(String newAuthorUsername) {
        this.authorUsername = newAuthorUsername;
    }
    public String getAuthorUsername() {
        return authorUsername;
    }

    public int getLikedCount() {
        return likedCount;
    }
    
    public boolean getLiked() {
        return liked;
    }

    public void setLikedCount(int likedCount) {
        this.likedCount = likedCount;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public void setContent(String newContent) {
        this.content = newContent;
    }
}