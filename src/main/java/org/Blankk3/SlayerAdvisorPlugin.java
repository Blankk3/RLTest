package org.Blankk3;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.callback.ClientThread;
import java.awt.image.BufferedImage;
import com.google.inject.Injector;

@Slf4j
@PluginDescriptor(
	name = "RLTest"
)
public class SlayerAdvisorPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private Injector injector;

	@Inject
	private SlayerAdvisorConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	private NavigationButton navButton;
	private SlayerAdvisorPanel panel;

	@Inject
	private ClientThread clientThread;

	@Override
	protected void startUp() throws Exception
	{
		log.debug("Slayer Advisor startUp() ran");

		panel = injector.getInstance(SlayerAdvisorPanel.class);

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");
		if (icon == null)
		{
			log.error("Could not load icon.png. Put it in src/main/resources/org/Blankk3/icon.png (or adjust the load path).");
			// You can still add the panel without an icon by returning early or using a fallback.
			return;
		}

		navButton = NavigationButton.builder()
				.tooltip("Slayer Advisor")
				.icon(icon)
				.priority(5)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);

		// ✅ Client calls must run on the client thread
		clientThread.invoke(() ->
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Slayer Advisor enabled", null)
		);

		log.debug("Slayer Advisor started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		if (clientToolbar != null && navButton != null)
		{
			clientToolbar.removeNavigation(navButton);
		}

		navButton = null;
		panel = null;

		log.debug("Slayer Advisor stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invoke(() ->
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "RLTest says " + config.greeting(), null)
			);
		}
	}

	@Provides
	SlayerAdvisorConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SlayerAdvisorConfig.class);
	}
}
