/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.MoreSaplings;

import java.io.File;
import java.util.EnumMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author cnaude
 */
public class MSMain extends JavaPlugin implements Listener {
    public static final String PLUGIN_NAME = "MoreSaplings";
    public static final String LOG_HEADER = "[" + PLUGIN_NAME + "]";
    static final Logger log = Logger.getLogger("Minecraft"); 
    private static Random randomGenerator;
        
    private File pluginFolder;
    private File configFile;
    
    private static EnumMap<TreeSpecies,Integer> dropChance = new EnumMap<TreeSpecies, Integer>(TreeSpecies.class);
    
    @Override
    public void onEnable() {
        randomGenerator = new Random();
        pluginFolder = getDataFolder();
        configFile = new File(pluginFolder, "config.yml");
        createConfig();
        this.getConfig().options().copyDefaults(true);
        saveConfig();
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);         
    }
        
    @EventHandler
    public void onLeavesDecayEvent(LeavesDecayEvent event) {
        Block block = event.getBlock();   
        World world = block.getWorld();
        Location loc = block.getLocation();
        byte b;
        
        if (block.getType().equals(Material.LEAVES)) {  
            byte data = block.getData();
            TreeSpecies species;
            if (data == (byte)0 || data == (byte)8) {
                species = TreeSpecies.GENERIC;
                b = (byte)0;
            } else if (data == (byte)1 || data == (byte)9) {
                species = TreeSpecies.REDWOOD;
                b = (byte)1;
            } else if (data == (byte)2 || data == (byte)10) {
                species = TreeSpecies.BIRCH;
                b = (byte)2;    
            } else if (data == (byte)3 || data == (byte)11) {
                species = TreeSpecies.JUNGLE;
                b = (byte)3;
            } else {
                species = TreeSpecies.GENERIC;
                b = (byte)0;
            }
            if (dropChance.containsKey(species)) {
                int chance = dropChance.get(species);  
                if (chance == 0) {
                    event.setCancelled(true);
                    block.setType(Material.AIR);
                } else if (chance >=0 && chance <= 100) {
                    int r = randomGenerator.nextInt(100);                                    
                    if (r <= chance) {                        
                        event.setCancelled(true);
                        block.setType(Material.AIR);
                        dropSapling(world, loc, b); 
                    }   
                }
            }
        }
    }
    
    public void dropSapling(World w, Location l, byte b) {
        ItemStack item = new ItemStack(Material.SAPLING, 1);
        item.setDurability(b);
        w.dropItemNaturally(l,item);            
    }
    
    private void createConfig() {
        if (!pluginFolder.exists()) {
            try {
                pluginFolder.mkdir();
            } catch (Exception e) {
                logInfo("ERROR: " + e.getMessage());                
            }
        }

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (Exception e) {
                logInfo("ERROR: " + e.getMessage());
            }
        }
    }
        
    private void loadConfig() {
        dropChance.put(TreeSpecies.GENERIC, getConfig().getInt("oak-drop-chance"));
        dropChance.put(TreeSpecies.REDWOOD, getConfig().getInt("spruce-drop-chance"));
        dropChance.put(TreeSpecies.BIRCH,   getConfig().getInt("birch-drop-chance"));
        dropChance.put(TreeSpecies.JUNGLE,  getConfig().getInt("jungle-drop-chance"));
    }
            
    public void logInfo(String _message) {
        log.log(Level.INFO, String.format("%s %s", LOG_HEADER, _message));
    }

    public void logError(String _message) {
        log.log(Level.SEVERE, String.format("%s %s", LOG_HEADER, _message));
    }
}
