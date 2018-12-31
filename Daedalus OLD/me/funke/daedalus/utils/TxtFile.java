package me.funke.daedalus.utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TxtFile {
    private File File;
    private String Name;
    private List<String> Lines = new ArrayList();

    public TxtFile(JavaPlugin Plugin, String Path, String Name) {
        this.File = new File(Plugin.getDataFolder() + Path);
        this.File.mkdirs();
        this.File = new File(Plugin.getDataFolder() + Path, Name + ".txt");
        try {
            this.File.createNewFile();
        } catch (IOException localIOException) {
        }
        this.Name = Name;

        readTxtFile();
    }

    public void clear() {
        this.Lines.clear();
    }

    public void addLine(String line) {
        this.Lines.add(line);
    }

    public void write() {
        try {
            FileWriter fw = new FileWriter(this.File, false);
            BufferedWriter bw = new BufferedWriter(fw);
            for (String Line : this.Lines) {
                bw.write(Line);
                bw.newLine();
            }
            bw.close();
            fw.close();
        } catch (Exception localException) {
        }
    }

    public void readTxtFile() {
        this.Lines.clear();
        try {
            FileReader fr = new FileReader(this.File);
            BufferedReader br = new BufferedReader(fr);
            String Line;
            while ((Line = br.readLine()) != null) {
                this.Lines.add(Line);
            }
            br.close();
            fr.close();
        } catch (Exception exx) {
            exx.printStackTrace();
        }
    }

    public String getName() {
        return this.Name;
    }

    public String getText() {
        String text = "";
        for (int i = 0; i < this.Lines.size(); i++) {
            String line = this.Lines.get(i);

            text = text + line + (this.Lines.size() - 1 == i ? "" : "\n");
        }
        return text;
    }

    public List<String> getLines() {
        return this.Lines;
    }
}
