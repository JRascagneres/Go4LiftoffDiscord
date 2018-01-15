package Permissions;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.List;

public class PermissionsChecker {

    public static boolean canRun(Member member, Channel channel, PermissionLevel requiredPermission){
        PermissionLevel userLevel = getPermissionLevel(member, channel);
        if(userLevel.getValue() >= requiredPermission.getValue()){
            return true;
        }
        return false;
    }

    public static PermissionLevel getPermissionLevel(Member member, Channel channel){
        if(member.getUser().getId().equals(String.valueOf(150768477152477186L))){
            return PermissionLevel.BotOwner;
        }

        if(member.getUser().equals(channel.getGuild().getOwner().getUser())){
            return PermissionLevel.ServerOwner;
        }

        List<Permission> serverPerms = member.getPermissions(channel);

        if(serverPerms.contains(Permission.MANAGE_ROLES)){
            return PermissionLevel.ServerAdmin;
        }
        if(serverPerms.contains(Permission.MESSAGE_MANAGE) && serverPerms.contains(Permission.KICK_MEMBERS) && serverPerms.contains(Permission.BAN_MEMBERS)){
            return PermissionLevel.ServerModerator;
        }

        if(member.getRoles().stream().map(Role::getName).anyMatch(name -> name.equalsIgnoreCase("Bot Manager"))){
            return PermissionLevel.BotManager;
        }

        return PermissionLevel.User;
    }

}
