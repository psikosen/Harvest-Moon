package joshie.harvestmoon.player;

import static joshie.harvestmoon.core.helpers.ServerHelper.markDirty;
import static joshie.harvestmoon.core.network.PacketHandler.sendToClient;

import java.util.HashSet;

import joshie.harvestmoon.core.network.quests.PacketQuestSetAvailable;
import joshie.harvestmoon.core.network.quests.PacketQuestSetCurrent;
import joshie.harvestmoon.core.util.IData;
import joshie.harvestmoon.init.HMQuests;
import joshie.harvestmoon.quests.Quest;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class QuestStats implements IData {
    private HashSet<Quest> finished = new HashSet();
    private HashSet<Quest> current = new HashSet(10);

    public PlayerDataServer master;

    public QuestStats(PlayerDataServer master) {
        this.master = master;
    }

    public HashSet<Quest> getCurrent() {
        return current;
    }

    private Quest getAQuest(Quest quest) {
        if (current != null) {
            for (Quest q : current) {
                if (q.equals(quest)) {
                    return q;
                }
            }
        }

        return null;
    }

    //Called to start a quest, is called clientside, by the startquest packet
    public boolean startQuest(Quest q) {
        if (current.size() < 10) {
            try {
                Quest quest = ((Quest) q.getClass().newInstance()).setName(q.getName()).setStage(0); //Set the current quest to your new 
                current.add(quest);
                syncQuest(q);
            } catch (Exception e) {}

            return true;
        } else return false;
    }

    public void setStage(Quest quest, int stage) {
        Quest q = getAQuest(quest);
        if (q != null) {
            q.setStage(stage);
        }

        markDirty();
    }

    //Quests should always REMOVE from the current quests, and add to the finished quests THEMSELVES
    public void markCompleted(Quest quest) {
        Quest q = getAQuest(quest);
        if (q != null) {
            q.claim(master.getAndCreatePlayer());
            finished.add(q);
            current.remove(q);
            syncQuests();
        }
    }

    public void syncQuests() {
        for (Quest quest : HMQuests.getQuests().values()) {
            syncQuest(quest);
        }
    }

    public void syncQuest(Quest quest) {
        //Check if the quest can be complete
        //If the quest isn't finished, do stuff
        if (!finished.contains(quest)) {
            //If the quest is in the currently active list, mark it as current
            if (current.contains(quest)) {
                //Send a packet, fetching the actual quest details that are saved, so we're update to date on the info
                sendToClient(new PacketQuestSetCurrent(getAQuest(quest)), master.getAndCreatePlayer());
            } else {
                //Now the quests aren't in the current list has been determined, let's determine whether this quest is valid for being collected
                //If the quest can be started, we should send it to client to be added to the available list
                if (quest.canStart(master.getAndCreatePlayer(), current, finished)) {
                    sendToClient(new PacketQuestSetAvailable(quest), master.getAndCreatePlayer());
                }
            }
        }

        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("CurrentQuests")) {
            NBTTagList list = nbt.getTagList("CurrentQuests", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                Quest q = HMQuests.get(tag.getString("QuestID"));
                try {
                    Quest quest = ((Quest) q.getClass().newInstance()).setName(q.getName());
                    quest.readFromNBT(tag);
                    current.add(quest);
                } catch (Exception e) {}
            }
        }

        if (nbt.hasKey("FinishedQuests")) {
            NBTTagList list = nbt.getTagList("FinishedQuests", 8);
            for (int i = 0; i < list.tagCount(); i++) {
                finished.add(HMQuests.get(list.getStringTagAt(i)));
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        NBTTagList quests = new NBTTagList();
        for (Quest s : current) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("QuestID", s.getName());
            s.writeToNBT(tag);
            quests.appendTag(tag);
        }

        nbt.setTag("CurrentQuests", quests);

        NBTTagList done = new NBTTagList();
        for (Quest s : finished) {
            if (s != null) {
                done.appendTag(new NBTTagString(s.getName()));
            }
        }

        nbt.setTag("FinishedQuests", done);
    }
}
