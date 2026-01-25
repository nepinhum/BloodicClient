package net.bloodic.commands;

import net.bloodic.BloodicClient;
import net.bloodic.command.BaseCommand;
import net.bloodic.command.CmdException;
import net.bloodic.utils.ChatUtils;

import java.util.Objects;

public class IpCommand extends BaseCommand
{
    public IpCommand()
    {
        super("ip", "Shows the IP of the server you are currently.", ".ip");
    }

    private String getIP()
    {
        if(MC.isInSingleplayer())
            return "127.0.0.1:25565";

        assert MC.player != null;
        String ip = Objects.requireNonNull(MC.player.getServer()).getServerIp();
        if(!ip.contains(":"))
            ip += ":25565";

        return ip;
    }

    @Override
    public void execute(String[] args) throws CmdException
    {
        if (BloodicClient.MC == null || BloodicClient.MC.player == null)
            throw new CmdException("Player not available.");

        ChatUtils.info("Server IP is: " + getIP());
    }
}
