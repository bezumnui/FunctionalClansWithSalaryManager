package ru.oshifugo.functionalclans.events;

import me.ford.salarymanager.OnSalaryEvent;
import me.ford.salarymanager.SalaryReportPaymentsEvent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.oshifugo.functionalclans.sql.Clan;
import ru.oshifugo.functionalclans.sql.Member;
import ru.oshifugo.functionalclans.sql.SQLiteUtility;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

public class SalaryEvents implements EventListener, Listener {
    Map<String, Integer> clansTaxes;
    public SalaryEvents() {
        clansTaxes = new HashMap<>();
    }
    private void addTax(String clan, int tax) {
        int clanTaxesNow = 0;
        if (clansTaxes.containsKey(clan)){
            clanTaxesNow = clansTaxes.get(clan);
        }
        clansTaxes.put(clan, clanTaxesNow + tax);
    }
    private void clearTaxes() {
        clansTaxes.clear();
    }
    @EventHandler
    public void onSalaryEvent(OnSalaryEvent event) {
        String clanName = Member.getClan(event.getPlayer().getName());
        if (clanName == null) return;
        int tax = Clan.getTax(clanName);
        if (tax == 0) return;
        double clanTaxes = event.getAmount() / 100 * tax;
        event.setAmount(event.getAmount() - clanTaxes);
        event.setShouldSendMessage(false);
        int clanCurrentCash = Clan.getCash(clanName);
        Clan.setCash(clanName, String.valueOf(clanCurrentCash + (int) clanTaxes));
        addTax(clanName, (int)clanTaxes);
        event.getPlayer().sendMessage(String.format(
                "§cВы заработали §b%s₴. §cНалог клана: §b%d₴. §cИтого вы получаете: §a%d₴!",
                (int) event.getAmount(), tax, (int)(event.getAmount())
        ));
    }

    @EventHandler
    public void  onSalaryReport(SalaryReportPaymentsEvent event) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            String clanName = Member.getClan(player.getName());
            if (clanName == null) continue;
            if (!clansTaxes.containsKey(clanName)) continue;
            int tax = clansTaxes.get(clanName);
            if (tax == 0) continue;
            player.sendMessage(String.format("§cВ казну клана §b%s§c упало: §b%d₴", clanName, tax));
        }
        clearTaxes();
    }
}

