package MessageHandler;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

public class MessageConstructor {
    EmbedBuilder embedBuilder = new EmbedBuilder();

    private JDA jda;
    private Color color = new Color(51, 153, 255);
    private Color failColor = new Color(255, 0, 0);
    private Color successColor = new Color(0, 255, 0);
    private String author;
    private String title;
    private String authorImageURL = "https://c1.staticflickr.com/1/735/32312416415_adf4f021b6_k.jpg";


    public MessageConstructor (String postName, JDA jda){
        author = postName;
        this.jda = jda;
    }

    public MessageConstructor (JDA jda){
        this.jda = jda;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setJDA(JDA jda){
        this.jda = jda;
    }

    public void addField(String title, String description){
        embedBuilder.addField(title, description, false);
    }

    public void appendDescription(String description) {
        embedBuilder.appendDescription(description);
    }

    public void setThumbnailURL(String thumbnailURL){
        embedBuilder.setThumbnail(thumbnailURL);
    }

    public void setColor(Color color){
        this.color = color;
    }

    public void setSuccessColor(){
        this.color = successColor;
    }

    public void setFailColor(){
        this.color = failColor;
    }

    public void setData(){
        embedBuilder.setAuthor(author, null, authorImageURL);
        embedBuilder.setColor(color);
        embedBuilder.setTitle(title);
    }

    public void sendMessageNoReset(Long channelID){
        setData();
        jda.getTextChannelById(channelID).sendMessage(embedBuilder.build()).queue();
    }

    public void sendMessage(Long channelID) {
        sendMessageNoReset(channelID);
        embedBuilder = new EmbedBuilder();
    }

    public void sendPrivate(User user){
        setData();
        user.openPrivateChannel().queue( (channel) -> channel.sendMessage(embedBuilder.build()).queue());
    }

}
