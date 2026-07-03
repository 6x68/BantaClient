package today.vanta.client.screen;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.Session;
import today.vanta.Vanta;
import today.vanta.client.event.impl.client.RenderScreenEvent;
import today.vanta.client.screen.component.Component;
import today.vanta.client.screen.component.impl.AccountComponent;
import today.vanta.client.screen.component.impl.ButtonComponent;
import today.vanta.util.client.network.MicrosoftUtil;
import today.vanta.util.client.network.account.Account;
import today.vanta.util.client.network.account.AccountSavingUtil;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.render.font.impl.GlyphFontRenderer;
import today.vanta.util.game.render.font.CFonts;
import today.vanta.util.game.render.shape.impl.Rectangle;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AltLoginScreen extends GuiScreen {
    private final GlyphFontRenderer smallTitle = CFonts.SFPT_SEMIBOLD_20;
    private final GlyphFontRenderer buttonText = CFonts.SFPT_MEDIUM_18;

    private final List<Component> components = new ArrayList<>();

    public AltLoginScreen() {
        AccountSavingUtil.loadConfig();
    }

    @Override
    public void initGui() {
        float middleX = width / 2f;
        float middleY = height / 2f;

        float buttonWidth = 140;

        components.clear();
        components.add(new ButtonComponent("Login with browser", middleX - buttonWidth / 2f, middleY, buttonWidth, 14, buttonText));
        middleY += 14;
        if (!AccountSavingUtil.ACCOUNTS.isEmpty()) {
            for (Account account : AccountSavingUtil.ACCOUNTS) {
                components.add(new AccountComponent(account, middleX - buttonWidth / 2f, middleY, buttonWidth, 14, buttonText));
                middleY += 14;
            }
        }

        components.add(new ButtonComponent("Back", middleX - buttonWidth / 2f, middleY, buttonWidth, 14, buttonText));

        Vanta.instance.eventBus.register(this);
    }

    @Override
    public void onGuiClosed() {
        Vanta.instance.eventBus.unregister(this);
    }

    private CompletableFuture<MicrosoftUtil.LoginResult> loginWithAccount(Account account, ExecutorService executor) {
        return MicrosoftUtil.login(account.token, account.refreshToken, executor).handle((result, error) -> {
            if (error == null) {
                return CompletableFuture.completedFuture(result);
            }

            if (!account.refreshToken.isEmpty()) {
                return MicrosoftUtil.acquireMSTokensFromRefreshToken(account.refreshToken, executor)
                        .thenComposeAsync(ms -> MicrosoftUtil.acquireXboxAccessToken(ms.accessToken, executor)
                                .thenComposeAsync(xbox -> MicrosoftUtil.acquireXboxXstsToken(xbox, executor), executor)
                                .thenComposeAsync(xsts -> MicrosoftUtil.acquireMCAccessToken(xsts.get("Token"), xsts.get("uhs"), executor), executor)
                                .thenComposeAsync(mc -> MicrosoftUtil.login(mc, ms.refreshToken, executor), executor), executor);
            }

            CompletableFuture<MicrosoftUtil.LoginResult> failed = new CompletableFuture<>();
            failed.completeExceptionally(error);
            return failed;
        }).thenCompose(future -> future);
    }

    @EventListen
    private void onRender(RenderScreenEvent event) {
        Rectangle
                .create(0, 0, width, height)
                .color(new Color(20, 20, 20))
                .push(event);

        buttonText.drawString("(Alt accounts) Left click to login, right click to delete.", 5, 5, new Color(125, 125, 125).getRGB());

        float middleX = width / 2f;
        float middleY = height / 2f;

        Rectangle
                .create(middleX - 143 / 2f, middleY - 16, 143, 14 * components.size() + 18)
                .color(new Color(30, 30, 30))
                .push(event);
        smallTitle.drawString(mc.session.getUsername(), middleX - 143 / 2f + 3, middleY - 18 + 4.5f, -1);

        components.forEach(c -> c.draw(event));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (Component c : components) {
            if (c.click(mouseX, mouseY, mouseButton)) {
                switch (c.text) {
                    case "Login with browser":
                        ExecutorService authExecutor = Executors.newSingleThreadExecutor();
                        MicrosoftUtil.acquireMSAuthCode(authExecutor)
                                .thenComposeAsync(result -> MicrosoftUtil.acquireMSTokensFromAuthCode(result.code, result.redirectUri, authExecutor), authExecutor)
                                .thenComposeAsync(ms -> MicrosoftUtil.acquireXboxAccessToken(ms.accessToken, authExecutor)
                                        .thenComposeAsync(xbox -> MicrosoftUtil.acquireXboxXstsToken(xbox, authExecutor), authExecutor)
                                        .thenComposeAsync(xsts -> MicrosoftUtil.acquireMCAccessToken(xsts.get("Token"), xsts.get("uhs"), authExecutor), authExecutor)
                                        .thenComposeAsync(mc -> MicrosoftUtil.login(mc, ms.refreshToken, authExecutor), authExecutor), authExecutor)
                                .thenAccept(result -> {
                                    Account account = new Account(result.session.getUsername(), result.session.getPlayerID(), result.session.getToken(), result.refreshToken);
                                    if (!AccountSavingUtil.ACCOUNTS.contains(account)) {
                                        AccountSavingUtil.ACCOUNTS.add(account);
                                    }

                                    mc.session = result.session;
                                    Vanta.instance.logger.info("Logged into {}! (microsoft)", result.session.getUsername());
                                    AccountSavingUtil.saveConfig();

                                    mc.addScheduledTask(this::initGui);
                                })
                                .exceptionally(error -> {
                                    Vanta.instance.logger.error("Failed to login due to {}", error.getMessage());
                                    return null;
                                })
                                .whenComplete((result, error) -> authExecutor.shutdown());
                        break;
                    case "Back":
                        mc.displayGuiScreen(new GuiMainMenu());
                        break;
                    default:
                        if (c instanceof AccountComponent) {
                            AccountComponent aC = (AccountComponent) c;
                            Iterator<Account> iterator = AccountSavingUtil.ACCOUNTS.iterator();
                            while (iterator.hasNext()) {
                                Account account = iterator.next();
                                if (aC.account.equals(account)) {
                                    if (mouseButton == 0) {
                                        AccountSavingUtil.CURRENT_ACCOUNT = account;
                                        if (account.isCracked()) {
                                            Vanta.instance.logger.info("Logged into {}! (cracked)", account.username);
                                            mc.session = new Session(account.username, "", "", "legacy");
                                        } else {
                                            ExecutorService refreshExecutor = Executors.newSingleThreadExecutor();
                                            loginWithAccount(account, refreshExecutor)
                                                    .thenAccept(result -> {
                                                        account.token = result.session.getToken();
                                                        if (result.refreshToken != null) {
                                                            account.refreshToken = result.refreshToken;
                                                        }

                                                        mc.session = result.session;
                                                        Vanta.instance.logger.info("Logged into {}! (microsoft)", account.username);
                                                        AccountSavingUtil.saveConfig();

                                                        mc.addScheduledTask(this::initGui);
                                                    })
                                                    .exceptionally(error -> {
                                                        Vanta.instance.logger.error("Failed to login due to {}", error.getMessage());
                                                        return null;
                                                    })
                                                    .whenComplete((result, error) -> refreshExecutor.shutdown());
                                        }
                                    } else {
                                        Vanta.instance.logger.info("Removed {}!", account.username);
                                        iterator.remove();
                                        initGui();
                                    }
                                    AccountSavingUtil.saveConfig();
                                }
                            }
                        }
                        break;
                }
            }
        }
    }
}