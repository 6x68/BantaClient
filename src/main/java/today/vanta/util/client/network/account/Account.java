package today.vanta.util.client.network.account;

import java.util.Objects;

public class Account {
    public String username, uuid;
    public String token = "", refreshToken = "";
    public String skin = getSteveHead();

    public Account(String username, String uuid) {
        this.username = username;
        this.uuid = uuid == null ? "" : uuid;
    }

    public Account(String username, String uuid, String token) {
        this(username, uuid);
        this.token = token;
    }

    public Account(String username, String uuid, String token, String refreshToken) {
        this(username, uuid);
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public boolean isCracked() {
        return uuid.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(username, account.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    private static String getSteveHead() {
        return "iVBORw0KGgoAAAANSUhEUgAAASwAAAEsCAIAAAD2HxkiAAAE1UlEQVR4nOzVu4rdZRuH4XcyazL58uGmylgIsbdQ+5BS3JTaWwS08BBEW0WxsRHEytIgFlqkSpFaC1uFuCGoKUIgRMMks2ZkWnufu7muE/it/wv3ejbPPfPYGnR0PLl2arNzZnLucHs0OXf6pEfbybkzZ0bfc621t9mdnDs5mVw7Nf2gwL+IEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCG2OTquf8J/7Ohk9Av/f+7s5Nxa64mzO5Nz27U7ObfWunP/cHJue7KdnHMJoSdCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGIihJgIIbYZ3vvo7TeGF/f3zk7O/e/c45Nza60Hf90b3dud/uO+++fvk3MffH1tcs4lhJ4IISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGI7X7775uTe+f39ybm11t+Hh5Nzu7vTH3j33t3JuYsHFybn1lq//vLj8OIwlxBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBim/P7+5N7r77zyeTcWuu1yx9Ozl25fHNybq118eDC5Nyn3x5Mzq21vrrx8eTc1ffempxzCaEnQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoTYzudXXpzcO3jq6cm5tdZm/8nJuW+uX5+cm/f6S5eGF2/99tPk3M3bdybnXELoiRBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIbe4/2k7uPbx1c3JurXX1xneTcy8/+8Lk3Lz3P/tiePGVS89Pzu2svck5lxB6IoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIbX7+4/bk3vHx8eTcvGs/fD+8uLu3GV4cdv/BdnLu8NHDyTmXEHoihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGIihJgIISZCiIkQYiKEmAghJkKIiRBiIoSYCCEmQoiJEGL/BAAA//+J4VhDvV4pYgAAAABJRU5ErkJggg==";
    }
}