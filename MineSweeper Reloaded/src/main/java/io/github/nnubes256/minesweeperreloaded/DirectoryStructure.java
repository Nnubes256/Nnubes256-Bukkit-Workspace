/*
* This is a modification of the directory structure handler of
* the Minecraft Bukkit plugin 'ChessCraft', plugin made by desht,
* and licensed under the GNU General Public License v3(GPLv3).
*
* TODO: License the whole project with same license.
*
* Now, a small talk:
* 'All rights reserved' sucks. A lot. Don't be jerk, and use
* a GPL license or whavether similar for your projects. Everyone likes
* to share its work. And if you do it, you'll be rewarded for it someday.
* With code, improvements, and that stuff. You'll be able to catch bugs a
* lot faster, especially when your project is famous.
*/

package io.github.nnubes256.minesweeperreloaded;

//import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FilenameFilter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.Reader;
//import java.io.Writer;
//import java.net.URL;
//import java.net.URLConnection;
//import java.nio.charset.Charset;

import me.desht.dhutils.LogUtils;
//import me.desht.dhutils.MiscUtil;

public class DirectoryStructure {
    //public static final Charset TARGET_ENCODING = Charset.forName("UTF-8");
    //public static final Charset SOURCE_ENCODING = Charset.forName("UTF-8");

    private static File pluginDir = new File("plugins", "Minesweeper Reloaded"); //$NON-NLS-1$ //$NON-NLS-2$
    private static File schematicsDir;
    //private static File boardStyleDir, pieceStyleDir;
    private static final String schematicsFoldername = "TEMP-SCHEMATICS"; //$NON-NLS-1$

    //private enum ExtractWhen { ALWAYS, IF_NOT_EXISTS, IF_NEWER };

    public static void setup() {
        pluginDir = Minesweeper.getInstance().getDataFolder();
        setupDirectoryStructure();
    }

    private static void setupDirectoryStructure() {
// directories
        schematicsDir = new File(pluginDir, schematicsFoldername);

// [plugins]/Minesweeper-Reloaded
        createDir(pluginDir);

// saved terrain schematics may need to be moved into their new location
        File oldSchematicsDir = new File(pluginDir, "schematics"); //$NON-NLS-1$
        if (oldSchematicsDir.isDirectory()) {
            if (!oldSchematicsDir.renameTo(schematicsDir)) {
                LogUtils.warning("Can't move " + oldSchematicsDir + " to " + schematicsDir); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } else {
// [plugins]/ChessCraft/data/boards/schematics
            createDir(schematicsDir);
        }
    }

    public static File getSchematicsDirectory() {
        return schematicsDir;
    }
/*
    public static File getJarFile() {
        File f = new File("plugins", "ChessCraft.jar");
        if (!f.exists()) {
            String ver = Minesweeper.getInstance().getDescription().getVersion();
            f = new File("plugins", "ChessCraft-" + ver + ".jar");
        }
        return f;
    }

    private static void extractResources() throws IOException {
        extractResource("/AI_settings.yml", pluginDir, ExtractWhen.IF_NEWER); //$NON-NLS-1$
        extractResource("/AI.yml", pluginDir, ExtractWhen.IF_NOT_EXISTS); //$NON-NLS-1$
        extractResource("/timecontrols.yml", pluginDir, ExtractWhen.IF_NOT_EXISTS); //$NON-NLS-1$

        for (String s : MiscUtil.listFilesinJAR(getJarFile(), "datafiles/board_styles",	".yml")) {
            extractResource(s, boardStyleDir);
        }

        for (String s : MiscUtil.listFilesinJAR(getJarFile(), "datafiles/piece_styles",	".yml")) {
            extractResource(s, pieceStyleDir);
        }

// message resources no longer extracted here - this is now done by Messages.loadMessages()
    }
*/
    private static void createDir(File dir) {
        if (dir.isDirectory()) {
            return;
        }
        if (!dir.mkdir()) {
            LogUtils.warning("Can't make directory " + dir.getName()); //$NON-NLS-1$
        }
    }
/*
    static void extractResource(String from, File to) {
        extractResource(from, to, ExtractWhen.IF_NEWER);
    }

    static void extractResource(String from, File to, ExtractWhen when) {
        File of = to;
        if (to.isDirectory()) {
            String fname = new File(from).getName();
            of = new File(to, fname);
        } else if (!of.isFile()) {
            LogUtils.warning("not a file: " + of);
            return;
        }

        LogUtils.finer("extractResource: file=" + of +
                ", file-last-mod=" + of.lastModified() +
                ", file-exists=" + of.exists() +
                ", jar-last-mod=" + getJarFile().lastModified() +
                ", when=" + when);

// if the file exists and is newer than the JAR, then we'll leave it alone
        if (of.exists() && when == ExtractWhen.IF_NOT_EXISTS) {
            return;
        }
        if (of.exists() && of.lastModified() > getJarFile().lastModified() && when != ExtractWhen.ALWAYS) {
            return;
        }

        if (!from.startsWith("/")) {
            from = "/" + from;
        }

        LogUtils.fine(String.format("extracting resource: %s (%s) -> %s (%s)", from, SOURCE_ENCODING.name(), to, TARGET_ENCODING.name()));

        final char[] cbuf = new char[1024];
        int read;
        try {
            final Reader in = new BufferedReader(new InputStreamReader(openResourceNoCache(from), SOURCE_ENCODING));
            final Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(of), TARGET_ENCODING));
            while ((read = in.read(cbuf)) > 0) {
                out.write(cbuf, 0, read);
            }
            out.close(); in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static InputStream openResourceNoCache(String resource) throws IOException {
        URL res = Minesweeper.class.getResource(resource);
        if (res == null) {
            LogUtils.warning("can't find " + resource + " in plugin JAR file"); //$NON-NLS-1$
            return null;
        }
        URLConnection resConn = res.openConnection();
        resConn.setUseCaches(false);
        return resConn.getInputStream();
    }

    /**
     * Find a YAML resource file in the given directory. Look first in the custom/ subdirectory
     * and then in the directory itself.
     *
     * @param dir
     * @param filename
     * @return
     *//*
    public static File getResourceFileForLoad(File dir, String filename) {
// try the lower-cased form first, if that fails try the exact filename
        File f = new File(dir, "custom" + File.separator + filename.toLowerCase() + ".yml");
        if (!f.exists()) {
            f = new File(dir, "custom" + File.separator + filename + ".yml");
        }
        if (!f.exists()) {
            f = new File(dir, filename.toLowerCase() + ".yml");
// if (!f.exists()) {
// throw new ChessException("Resource file '" + f + "' does not exist");
// }
        }
        return f;
    }

    /**
     * Check if the given file is a custom resource, i.e. it's a custom/ subdirectory.
     *
     * @param path
     * @return
     *//*
    public static boolean isCustom(File path) {
        return path.getParentFile().getName().equalsIgnoreCase("custom");
    }

    /**
     * Find a YAML resource in the custom/ subdirectory of the given directory.
     *
     * @param dir
     * @param filename
     * @return
     *//*
    public static File getResourceFileForSave(File dir, String filename) {
        File f = new File(dir, "custom" + File.separator + filename.toLowerCase() + ".yml");
        return f;
    }

    public static final FilenameFilter ymlFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".yml");
        }
    };
*/
}
