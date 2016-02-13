/*
The MIT License (MIT)

Copyright © 2016 12AwesomeMan34 / 12AwsomeMan34

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.awesomeman.repair;

import java.math.BigDecimal;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class RepairCommand implements CommandExecutor {
    
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "You must be a player to use this command!"));
            return CommandResult.empty();
        }
        
        Player player = (Player) src;
        
        if(!player.getHelmet().isPresent() && !player.getChestplate().isPresent()
                && !player.getLeggings().isPresent() && !player.getBoots().isPresent()) {
            src.sendMessage(Text.of(TextColors.RED, "You are not wearing any armour!"));
            return CommandResult.empty();
        }
        
        if(player.getHelmet().isPresent()) {
            ItemStack helmet = player.getHelmet().get();
            DurabilityData data = helmet.get(DurabilityData.class).get();
            helmet.offer(Keys.ITEM_DURABILITY, data.durability().getMaxValue());
            player.setHelmet(helmet);
        }
        if(player.getChestplate().isPresent()) {
            ItemStack chestplate = player.getChestplate().get();
            DurabilityData data = chestplate.get(DurabilityData.class).get();
            chestplate.offer(Keys.ITEM_DURABILITY, data.durability().getMaxValue());
            player.setChestplate(chestplate);
        }
        if(player.getLeggings().isPresent()) {
            ItemStack leggings = player.getLeggings().get();
            DurabilityData data = leggings.get(DurabilityData.class).get();
            leggings.offer(Keys.ITEM_DURABILITY, data.durability().getMaxValue());
            player.setLeggings(leggings);
        }
        if(player.getBoots().isPresent()) {
            ItemStack boots = player.getBoots().get();
            DurabilityData data = boots.get(DurabilityData.class).get();
            boots.offer(Keys.ITEM_DURABILITY, data.durability().getMaxValue());
            player.setBoots(boots);
        }
        
        double cost = ArmourRepair.instance.cost;
        EconomyService economy = ArmourRepair.instance.economy;
        if(economy != null) {
            Optional<UniqueAccount> optional = economy.getAccount(player.getUniqueId());
            if(optional.isPresent()) {
                UniqueAccount account = optional.get();
                account.withdraw(economy.getDefaultCurrency(), BigDecimal.valueOf(cost), Cause.of(src));
                String currency = cost == 1 ?
                        economy.getDefaultCurrency().getDisplayName().toPlain().toLowerCase() :
                        economy.getDefaultCurrency().getPluralDisplayName().toPlain().toLowerCase();
                src.sendMessage(Text.of(TextColors.GREEN, "Success! ", TextColors.GOLD, "Repaired your equipped armour and deducted " + cost + " ", currency, " from your account balance."));
            } else {
                src.sendMessage(Text.of(TextColors.RED, "Account balance not availible."));
            }
        } else {
            src.sendMessage(Text.of(TextColors.GREEN, "Success! ", TextColors.GOLD, "Repaired your equipped armour."));
        }
        return CommandResult.success();
    }   
}
