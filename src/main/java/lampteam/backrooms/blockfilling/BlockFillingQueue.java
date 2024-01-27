package lampteam.backrooms.blockfilling;

import lampteam.backrooms.Backrooms;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

// Очередь запросов на изменение блоков с помощью FAWE
public class BlockFillingQueue {

    List<BlockFillingRequest> queue = new ArrayList<>();

    // ИНициализация
    public void init(){

        ConsoleCommandSender console = Bukkit.getConsoleSender();
        Bukkit.dispatchCommand(console, "/world world");

        // Обрабока очереди (до 4х запросов в секунду)
        BukkitRunnable queueProcessor = new BukkitRunnable() {
            @Override
            public void run() {
                if (queue.size() == 0){
                    return;
                }
                BlockFillingRequest currentRequest = queue.get(0);
                Bukkit.dispatchCommand(console, "/pos1 " + currentRequest.getPos1()[0] + "," + currentRequest.getPos1()[1] + "," + currentRequest.getPos1()[2]);
                Bukkit.dispatchCommand(console, "/pos2 " + currentRequest.getPos2()[0] + "," + currentRequest.getPos2()[1] + "," + currentRequest.getPos2()[2]);
                Bukkit.dispatchCommand(console, "/replace " + currentRequest.getOldBlocks() + " " + currentRequest.getNewBlocks());
                queue.remove(0);
            }
        };
        queueProcessor.runTaskTimer(Backrooms.getPlugin(), 0, 5);
    }

    // Добавление запроса в очередь
    public void addRequest(BlockFillingRequest request){
        queue.add(request);
    }

}
