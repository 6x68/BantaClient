/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.florianmichael.viamcp;

import com.viaversion.viabackwards.protocol.v1_17to1_16_4.Protocol1_17To1_16_4;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.v1_16_1to1_16_2.packet.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.v1_16_1to1_16_2.packet.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.v1_16_4to1_17.packet.ClientboundPackets1_17;
import com.viaversion.viaversion.protocols.v1_16_4to1_17.packet.ServerboundPackets1_17;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.gui.AsyncVersionSlider;

import java.io.File;

public class ViaMCP {
    public final static int NATIVE_VERSION = 47;
    public static ViaMCP INSTANCE;
    public UserConnection user;

    public static void create() {
        INSTANCE = new ViaMCP();
    }

    private AsyncVersionSlider asyncVersionSlider;

    public ViaMCP() {
        ViaLoadingBase.ViaLoadingBaseBuilder.create().runDirectory(new File("ViaMCP")).nativeVersion(NATIVE_VERSION).onProtocolReload(protocolVersion -> {
            if (getAsyncVersionSlider() != null) {
                getAsyncVersionSlider().setVersion(protocolVersion.getVersion());
            }
        }).build();

        fixTransactions();
    }

    private void fixTransactions() {
        // We handle the differences between those versions in the net code, so we can make the Via handlers pass through
        Via.getManager().getProtocolManager().addMappingLoaderFuture(Protocol1_17To1_16_4.class, () -> {
            final Protocol1_17To1_16_4 protocol = Via.getManager().getProtocolManager().getProtocol(Protocol1_17To1_16_4.class);
            if (protocol == null) return;

            new Thread(() -> {
                try {
                    for (int i = 0; i < 500; i++) {
                        if (protocol.hasRegisteredClientbound(ClientboundPackets1_17.PING)) {
                            protocol.replaceClientbound(ClientboundPackets1_17.PING, wrapper -> {
                                wrapper.setPacketType(ClientboundPackets1_16_2.CONTAINER_ACK);
                            });
                            protocol.replaceServerbound(ServerboundPackets1_16_2.CONTAINER_ACK, wrapper -> {
                                wrapper.setPacketType(ServerboundPackets1_17.PONG);
                            });
                            return;
                        }
                        Thread.sleep(10);
                    }
                } catch (InterruptedException ignored) {
                }
            }).start();
        });
    }

    public void initAsyncSlider() {
        this.initAsyncSlider(5, 5, 110, 20);
    }

    public void initAsyncSlider(int x, int y, int width, int height) {
        asyncVersionSlider = new AsyncVersionSlider(-1, x, y, Math.max(width, 110), height);
    }

    public AsyncVersionSlider getAsyncVersionSlider() {
        return asyncVersionSlider;
    }
}