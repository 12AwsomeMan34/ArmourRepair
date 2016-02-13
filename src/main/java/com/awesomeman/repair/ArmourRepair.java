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

import java.io.File;
import java.io.IOException;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

import com.google.inject.Inject;

@Plugin(name = "ArmourRepair", id = "ArmourRepair", version = ArmourRepair.VERSION)
public class ArmourRepair {
    
    public double cost;
    public static ArmourRepair instance;
    public EconomyService economy;
    protected static final String VERSION = "1.0";
    private CommentedConfigurationNode node;
    private @Inject @DefaultConfig(sharedRoot = true) File config;
    private @Inject @DefaultConfig(sharedRoot = true) ConfigurationLoader<CommentedConfigurationNode> loader;
    private @Inject Logger logger;
    
    @Listener
    public void preInit(GamePreInitializationEvent event) {
        logger.info("Initializing ArmourRepair version " + VERSION);
        
        instance = this;
        
        try {
            if(!config.exists()) {
                config.createNewFile();
                node = loader.load();
                node.getNode("cost").setValue(5).setComment("The cost for repairing armour.");
                loader.save(node);
            } else {
                node = loader.load();
            }
            cost = node.getNode("cost").getDouble(5);
        } catch(IOException e) {
            logger.error("Error loading configuration file.");
            e.printStackTrace();
        }
    }
    
    @Listener
    public void init(GameInitializationEvent event) {
        logger.info("Initializing commands.");
        
        CommandSpec repair = CommandSpec.builder()
                .permission("armour.repair")
                .description(Text.of("Repairs your armour for a small fee."))
                .executor(new RepairCommand())
                .build();
        
        Sponge.getCommandManager().register(this, repair, "armourrepair", "armorrepair", "armour-repair", "armor-repair");
        logger.info("ArmourRepair has successfully loaded.");
    }
    
    @Listener
    public void onChangeServerProvider(ChangeServiceProviderEvent event) {
        if(event.getService().equals(EconomyService.class)) {
            economy = (EconomyService) event.getNewProviderRegistration().getProvider();
        }
    }
}
