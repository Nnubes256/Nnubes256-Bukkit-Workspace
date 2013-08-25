package io.github.nnubes256.minesweeperreloaded;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Minesweeper.class, Player.class})
public class Tests {
    @Before
    public void setup() {
        Player commandTesterPlayerMock = PowerMockito.mock(Player.class);
        Minesweeper pluginToMock = PowerMockito.mock(Minesweeper.class);
        GameHandler GHMock = PowerMockito.mock(GameHandler.class);
        CommandHandling CH = new CommandHandling(pluginToMock, GHMock);

        when(pluginToMock.getCHInstance()).thenReturn(CH);

    }
}
