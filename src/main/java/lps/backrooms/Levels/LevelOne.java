package lps.backrooms.Levels;

import lps.backrooms.blockfilling.BlockFillingRequest;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "deprecation", "DataFlowIssue", "SpellCheckingInspection"})
public class LevelOne extends Arena{

    // Из конфига
    int initialMonsterAmount;
    int gasStationsAmount;
    int generatorsAmount;
    int whrenchAmount;
    int methAmount;
    int genFillingAmount;
    int lightsOutDuration;
    int generatorsRequired;
    Integer[] lightsFillPos1;
    Integer[] lightsFillPos2;

    // Служебные
    BoundingBox boundingBox;
    ArrayList<Entity> generators = new ArrayList<>();
    boolean isDark = false;
    boolean stopDarkness = false;
    boolean allGensUp = false;

    // Получеие локальных переменных
    public void initFromCfgLocal(int initialMonsterAmount, int whrenchAmount, int methAmount, int gasStationsAmount, int generatorsAmount, int genFillingAmount, int lightsOutDuration, int generatorsRequired, Integer[] lightsFillPos1, Integer[] lightsFillPos2){

        this.initialMonsterAmount = initialMonsterAmount;
        this.whrenchAmount = whrenchAmount;
        this.methAmount = methAmount;
        this.gasStationsAmount = gasStationsAmount;
        this.generatorsAmount = generatorsAmount;
        this.generatorsRequired = generatorsRequired;
        this.genFillingAmount = genFillingAmount;
        this.lightsOutDuration = lightsOutDuration;
        this.lightsFillPos1 = lightsFillPos1;
        this.lightsFillPos2 = lightsFillPos2;
        boundingBox = new BoundingBox(lightsFillPos1[0], floorsY.get(0), lightsFillPos1[2], lightsFillPos2[0], floorsY.get(0)+5, lightsFillPos2[2]);

    }

    // Спавн вещей
    private void spawnThings(){
        // Гаечные ключи
        for (int i = 0; i < whrenchAmount; i++){
            Location loc = getRandomPos(24, 0, borders, -1);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "clone 3 255 0 3 255 0 " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " minecraft:air destroy");
        }

        // Таблетки
        for (int i = 0; i < methAmount; i++){
            Location loc = getRandomPos(24, 0, borders, -1);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "clone 4 255 0 4 255 0 " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " minecraft:air destroy");
        }

        // СПАВН колонок
        for (int I = 0; I < gasStationsAmount; I++){
            Location loc = getRandomPos(40, 1, borders, 1);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm m spawn gasStation 1 " + "world" + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ",0,0");
        }

        // СПАВН генераторов
        for (int I = 0; I < generatorsAmount; I++){
            Location loc = getRandomPos(40, 1, borders, 1);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm m spawn generator 1 " + "world" + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ",0,0");
        }

        // СПАВН МОНСТРОВ
        for (int I = 0; I < players.size() + initialMonsterAmount; I++){
            Location loc = getRandomPos(40, 1, borders, 1);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm m spawn faceling 1 " + "world" + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ",0,0");
        }

        // Добавляем генераторы
        generators.addAll(Objects.requireNonNull(Bukkit.getWorld("world")).getNearbyEntities(boundingBox, (entity) -> entity.getType() == EntityType.PIG));
        for (Entity gen : generators){
            gen.setMetadata("time_left", new FixedMetadataValue(plugin, 0));
        }
    }

    // Темнота
    public void lightsOut(){
        isDark = true;

        // Выключение генераторов
        for (Entity gen : generators){
            gen.setMetadata("time_left", new FixedMetadataValue(plugin, 0));
        }

        // Эффект слепоты и звук для игроков
        for (Player player : players){
            player.playSound(player, Sound.ENTITY_ENDER_DRAGON_FLAP, 100, 1);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, lightsOutDuration*20, 1, false, false, false));
        }

        // Выключение света
        BlockFillingRequest disableLight = new BlockFillingRequest();
        disableLight.init(lightsFillPos1, lightsFillPos2, "redstone_lamp[lit=true]", "redstone_lamp[lit=false]");
        plugin.getBlockFillingQueue().addRequest(disableLight);

        BukkitRunnable darknessTimer = new BukkitRunnable() {
            int time = 0;
            @Override
            public void run() {

                time += 1;

                // Спавн Улыбающихся
                if (time % 2 == 0){
                    for (Player p : players){
                        List<Integer> playerCords = new ArrayList<>();
                        playerCords.add(p.getLocation().getBlockX() - 5);
                        playerCords.add(p.getLocation().getBlockX() + 5);
                        playerCords.add(p.getLocation().getBlockZ() - 5);
                        playerCords.add(p.getLocation().getBlockZ() + 5);
                        Location loc = getRandomPos(0, 0, playerCords, -1);
                        if (loc != null){
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm m spawn smiler 1 " + "world" + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ",0,0");
                        }
                    }
                }

                // Конец темноты
                if (currentTime == maxTime || players.size() == 0 || time == lightsOutDuration || stopDarkness){

                    isDark = false;
                    stopDarkness = false;

                    // Убийсвто Улыбающихся
                    List<Entity> smilers = new ArrayList<>(Objects.requireNonNull(Bukkit.getWorld("world")).getNearbyEntities(boundingBox, (entity) -> entity.getType() == EntityType.ZOMBIE));
                    for (Entity smiler : smilers){
                        smiler.remove();
                    }

                    // Включение света
                    BlockFillingRequest enableLight = new BlockFillingRequest();
                    enableLight.init(lightsFillPos1, lightsFillPos2, "redstone_lamp[lit=false]", "50%redstone_lamp[lit=false],50%redstone_lamp[lit=true]");
                    plugin.getBlockFillingQueue().addRequest(enableLight);
                    this.cancel();

                    for (Player player : players){
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                    }
                }
            }
        };
        darknessTimer.runTaskTimer(plugin, 0, 20);
    }

    // Попытка заправить генератор
    public void generatorFill(Player p, Entity generator){
        // Если идёт темнотта
        if (isDark){
            p.sendMessage("Слишком темно! Я не могу заправить генератор...");
            return;
        }

        // Если нет угля
        if (!p.getInventory().contains(Material.COAL)){
            // И генератор работает
            if (generator.getMetadata("time_left").get(0).asInt() > 0){
                p.sendActionBar("Этот генератор будет работать ещё " + generator.getMetadata("time_left").get(0).asInt() + " секунд. Найди топливо, чтобы его заправить!");
                return;
            }
            // Не работает
            p.sendActionBar("Этот генератор не работает. Найди топливо, чтобы его заправить!");
            return;
        }

        // Заправка генератора
        p.getInventory().remove(Material.COAL);
        generator.setMetadata("time_left", new FixedMetadataValue(plugin, genFillingAmount));

        // Подсчёт уже заправленных | TODO можно оптимизировать через добавление при заправке и ресет при темноте
        int gensLeft = generatorsRequired;
        for (Entity gener : generators){
            if (gener.getMetadata("time_left").get(0).asInt() > 1){
                gensLeft--;
            }
        }

        if (gensLeft == 0){
            allGensUp = true;
            for (Player player : players){
                player.sendMessage(ChatColor.GREEN + "Все генераторы заправлены! Найди лифт как можно скорее");
                return;
            }
        }

        for (Player player : players){
            if (player.equals(p)){
                p.sendMessage("Генератор заправлен! Осталось заправить ещё " + ChatColor.YELLOW + gensLeft + ChatColor.WHITE + " генераторов");
            } else {
                player.playSound(player, Sound.BLOCK_BELL_USE, SoundCategory.MASTER, 1, 0.5f);
                player.sendMessage("Игрок " + p.getName() + " заправил генератор! Осталось заправить ещё " + ChatColor.YELLOW + gensLeft + ChatColor.WHITE + " генераторов");
            }
        }
    }

    // Попытка победить
    @Override
    public void win(Player p) {

        List<Entity> currentGens = new ArrayList<>();

        for (Entity generator : generators){
            if (generator.getMetadata("time_left").get(0).asInt() > 0){
                currentGens.add(generator);
            }
        }

        if (currentGens.size() < generatorsRequired){
            p.sendMessage(ChatColor.RED + "Чтобы победить, вы должны запустить " + ChatColor.YELLOW + generatorsRequired + ChatColor.RED + " генераторов(-а)!");
            return;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi usermeta " + p.getName() + " increment level1_wins +1");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi toast " + p.getName() + " -t:challenge -icon:white_concrete &7Покинуть первый уровень");

        super.win(p);
    }

    // Спавн игрока
    @Override
    protected void spawnPlayer(Player p, Location pos){
        super.spawnPlayer(p, pos);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi kit level1_start " + p.getName());
    }

    @Override
    public void startGame(){

        spawnThings();
        for (Player p : players){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi usermeta " + p.getName() + " increment level1_games +1");
        }

        // Отсчёт времени для заправленных генераторов
        BukkitRunnable generatorsTicking = new BukkitRunnable() {
            @Override
            public void run() {

                // Работа с генераторами
                for (Entity generator : generators){
                    if (generator.getMetadata("time_left").get(0).asInt() <= 0){
                        continue;
                    } else if (generator.getMetadata("time_left").get(0).asInt() == 1) {
                        lightsOut();
                        break;
                    }
                    generator.setMetadata("time_left", new FixedMetadataValue(plugin, generator.getMetadata("time_left").get(0).asInt() - 1));
                }

                // Выключение
                if (currentTime == maxTime || players.size() == 0 || allGensUp){
                    this.cancel();
                }
            }
        };
        generatorsTicking.runTaskTimer(plugin, 0, 20);

        super.startGame();

    }

    @Override
    public void stopGame(){

        isDark = false;
        stopDarkness = false;
        generators.clear();
        allGensUp = false;

        super.stopGame();

    }

    public void methUse(Player player){
        player.getInventory().removeItem(new ItemStack(Material.PINK_DYE, 1));
        int chance = random.nextInt(1, 12);
        switch (chance){
            case (1):
                // Очистка инвенторя
                player.sendTitle("", "Gulp!", 10, 20, 10);
                player.getInventory().clear();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi kit level1_start " + player.getName());
                break;

            case (2):
                // Убийство игрока
                player.sendTitle("", "Bad trip", 10, 20, 10);
                BukkitRunnable pillDeath = new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setHealth(0);
                    }
                };
                pillDeath.runTaskLater(plugin, 60);
                break;

            case (3):
                // Отрыжка (ничего)
                player.sendTitle("", "I found pills...", 10, 20, 10);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, SoundCategory.MASTER, 100, 1);
                break;

            case (4):
                // Телепорт в случайное место
                player.sendTitle("", "Telepills", 10, 20, 10);
                player.teleport(getRandomPos(0, 0, borders, -1));
                break;

            case (5):
                // Ночное зрение
                player.sendTitle("", "I can see forever!", 10, 20, 10);
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 9999*20, 1, false, false, true));
                break;

            case (6):
                // Свечение
                player.sendTitle("", "Friends till the end!", 10, 20, 10);
                for (Player p : players){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 9999*20, 1, false, false, false));
                }
                break;

            case (7):
                // Обездвиживание
                player.sendTitle("", "Paralysis", 10, 20, 10);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10*20, 1, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10*20, 99, false, false, false));
                break;

            case (8):
                // Скорость + неуязвимость
                player.sendTitle("", "POWER PILL", 10, 20, 10);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10*20, 6, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 10*20, 99, false, false, false));
                break;

            case (9):
                // Постоянное увелечение скорости
                player.sendTitle("", "Speed up", 10, 20, 10);
                if (player.getPotionEffect(PotionEffectType.SPEED) == null){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999*20, 1, false, false, false));
                    break;
                }
                switch (player.getPotionEffect(PotionEffectType.SPEED).getAmplifier()){
                    case (1):
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999*20, 2, false, false, false));
                    case (2):
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999*20, 3, false, false, false));
                }
                break;

            case (10):
                // Постоянное замедление
                player.sendTitle("", "Speed down", 10, 20, 10);
                if (player.getPotionEffect(PotionEffectType.SLOW) == null){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999*20, 1, false, false, false));
                    break;
                }
                switch (player.getPotionEffect(PotionEffectType.SLOW).getAmplifier()){
                    case (1):
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999*20, 2, false, false, false));
                    case (2):
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999*20, 3, false, false, false));
                }
                break;

            case (11):
                // Невозможность подбирать предметы
                player.sendTitle("", "Somethin' wrong...", 10, 20, 10);
                player.setMetadata("can_pickup_items", new FixedMetadataValue(plugin, false));
                BukkitRunnable pickupDebuffCounter = new BukkitRunnable() {

                    int time = 0;

                    @Override
                    public void run() {

                        time += 1;

                        // ПРОВЕРКА НА ЗАВЕРШЕНИЕ ИГРЫ или истечение двух минут
                        if (currentTime == maxTime || players.size() == 0 || time == 120){
                            player.setMetadata("can_pickup_items", new FixedMetadataValue(plugin, true));
                            this.cancel();
                        }
                    }
                };
                pickupDebuffCounter.runTaskTimer(plugin, 0, 20);
                break;
        }

    }

    public void wrenchUse(Player player){
        int chance = random.nextInt(1, 12);
        player.getInventory().removeItem(new ItemStack(Material.STICK, 1));
        List<Entity> facelings = (List<Entity>) Bukkit.getWorld("world").getNearbyEntities(boundingBox, (entity) -> entity.getType() == EntityType.DROWNED);
        switch (chance){
            case (1):
                // Подсвет генеров
                player.sendTitle("", "Generators!", 10, 20, 10);
                for (Entity gen : generators){
                    LivingEntity tempGen = (LivingEntity) gen;
                    tempGen.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 15*20, 1, false, false, false));
                }
                break;

            case (2):
                // Спавн колонок
                player.sendTitle("", "More fuel!", 10, 20, 10);
                for (int I = 0; I < 3; I++){
                    Location loc = getRandomPos(40, 1, borders, 1);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm m spawn gasStation 1 " + "world" + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ",0,0");
                }
                break;

            case (3):
                // Деспавн колонок
                player.sendTitle("", "No fuel?", 10, 20, 10);
                List<Entity> gasStations = (List<Entity>) Bukkit.getWorld("world").getNearbyEntities(boundingBox, (entity) -> entity.getType() == EntityType.CHICKEN);
                if (gasStations.size() < 3){
                    break;
                }
                for (int i = 0; i < 3; i++){
                    gasStations.get(0).remove();
                }
                break;

            case (4):
                // Выключение всех генераторов
                player.sendTitle("", "Darkness incoming...", 10, 20, 10);
                lightsOut();
                break;

            case (5):
                // Останока тьмы
                player.sendTitle("", "The Light!", 10, 20, 10);
                stopDarkness = true;
                break;

            case (6):
                // Телепорт безликих в одно место
                player.sendTitle("", "Facelings party", 10, 20, 10);
                Location loc = getRandomPos(0, 1, borders, 1);
                for (Entity faceling : facelings){
                    faceling.teleport(loc);
                    LivingEntity faceling_temp = (LivingEntity) faceling;
                    faceling_temp.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30*5, 5, false, false, false));
                }
                break;

            case (7):
                // Мега-заправка работающих генеров
                player.sendTitle("", "POOOWEEER!", 10, 20, 10);
                for (Entity gen : generators){
                    if (gen.getMetadata("time_left").get(0).asInt() <= 0){
                        continue;
                    }
                    gen.setMetadata("time_left", new FixedMetadataValue(plugin, genFillingAmount*2));
                }
                break;

            case (8):
                // Metal pipe
                player.sendTitle("", "☠ BRUH ☠", 10, 20, 10);
                player.playSound(player, Sound.BLOCK_ANVIL_PLACE, SoundCategory.MASTER, 100, 1);
                break;

            case (9):
                // Теп безликой к игроку
                player.sendTitle("", "Unintended consequences", 10, 20, 10);
                Location location = player.getLocation();
                BukkitRunnable facelingTeleportDelay = new BukkitRunnable() {
                    @Override
                    public void run() {
                        facelings.get(0).teleport(location);
                    }
                };
                facelingTeleportDelay.runTaskLater(plugin, 20);
                break;

            case (10):
                // Увеличение скорости безликих на 30 сек
                player.sendTitle("", "They're fast!", 10, 20, 10);
                for (Entity faceling : facelings){
                    LivingEntity faceling_living = (LivingEntity) faceling;
                    faceling_living.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30*20, 1, false, false, false));
                }
                break;

            case (11):
                // Стан безликих на 15 сек
                player.sendTitle("", "Bed time", 10, 20, 10);
                for (Entity faceling : facelings){
                    LivingEntity faceling_living = (LivingEntity) faceling;
                    faceling_living.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 15*20, 10, false, false, false));
                }
                break;
        }
    }
}
