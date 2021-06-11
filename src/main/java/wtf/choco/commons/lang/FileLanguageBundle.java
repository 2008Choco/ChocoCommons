package wtf.choco.commons.lang;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link LanguageBundle} capable of being saved and loaded to and from file.
 *
 * @author Parker Hawke - Choco
 */
public class FileLanguageBundle extends MemoryLanguageBundle {

    private static final String SEPARATOR_CHAR = ":";
    private static final Pattern LANG_ENTRY_PATTERN = Pattern.compile("((?:\\w+\\.+[^.])*\\w+)\\s*" + SEPARATOR_CHAR + "\\s*\"(.*)\"");

    @Override
    public void loadFromString(@NotNull String string) {
        String[] lines = string.split("\n");

        for (int lineNumber = 1; lineNumber <= lines.length; lineNumber++) {
            String line = lines[lineNumber - 1];

            if (!LANG_ENTRY_PATTERN.matcher(line).matches()) {
                continue;
            }

            String[] parts = line.split("\\s*" + SEPARATOR_CHAR + "\\s*", 2);
            if (parts.length == 1) {
                throw new UnsupportedOperationException("Malformatted lang entry on line " + lineNumber + ". Missing separator character, \"" + SEPARATOR_CHAR + "\"");
            }

            String key = parts[0], value = parts[1];
            this.addGlobalPlaceholder(key, value);
        }
    }

    @NotNull
    @Override
    public String saveToString() {
        StringBuilder string = new StringBuilder();

        this.asMap().forEach((key, value) -> {
            string.append(key).append(SEPARATOR_CHAR).append(" \"").append(value).append("\"");
            string.append('\n');
        });

        return string.toString();
    }

    /**
     * Load data from the given {@link Reader} into this language bundle.
     *
     * @param reader the reader
     *
     * @throws IOException if an io error occurs
     */
    public void load(@NotNull Reader reader) throws IOException {
        Preconditions.checkArgument(reader != null, "reader must not be null");

        BufferedReader input = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
        StringBuilder builder = new StringBuilder();

        try {
            String line;
            while ((line = input.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        } finally {
            input.close();
        }

        this.loadFromString(builder.toString());
    }

    /**
     * Load data from the given {@link File} into this language bundle.
     * <p>
     * This method will load all files under the UTF-8 charset. If another charset is desired,
     * {@link #load(Reader)} should be used instead.
     *
     * @param file the file from which to load data
     *
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException if an io error occurs
     */
    public void load(@NotNull File file) throws FileNotFoundException, IOException {
        Preconditions.checkArgument(file != null, "file must not be null");

        FileInputStream stream = new FileInputStream(file);
        this.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    /**
     * A convenience method similar to {@link #load(File)}. Load data from the given
     * {@link File} into this language bundle and clear any previously loaded data.
     * <p>
     * This method is equivalent to doing:
     * <pre>
     * languageBundleFile.clear();
     * try {
     *     languageBundleFile.load(file);
     * } catch (IOException e) {
     *     e.printStackTrace();
     * }
     * </pre>
     *
     * @param file the file from which to load data
     */
    public void reload(@NotNull File file) {
        Preconditions.checkArgument(file != null, "file must not be null");

        this.clear();

        try {
            this.load(file);
        } catch (FileNotFoundException e) { // We're going to ignore file not found exceptions
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save data from this language bundle into the given file. If the file does not exist, a
     * file will be created.
     *
     * @param file the destination file
     *
     * @throws IOException if an io error occurs
     */
    public void save(@NotNull File file) throws IOException {
        Preconditions.checkArgument(file != null, "file must not be null");

        Files.createParentDirs(file);

        String data = saveToString();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8)) {
            writer.write(data);
        }
    }

    /**
     * Create and load a new {@link FileLanguageBundle} from the given file.
     *
     * @param file the file from which to load a language bundle
     *
     * @return the language bundle
     *
     * @see #load(File)
     */
    @NotNull
    public static FileLanguageBundle loadFromFile(@NotNull File file) {
        Preconditions.checkArgument(file != null, "file must not be null");

        FileLanguageBundle language = new FileLanguageBundle();

        try {
            language.load(file);
        } catch (FileNotFoundException e) { // We're going to ignore file not found exceptions
        } catch (IOException e) {
            e.printStackTrace();
        }

        return language;
    }

    /**
     * Create and load a new {@link FileLanguageBundle} from the given reader.
     *
     * @param reader the reader from which to load a language bundle
     *
     * @return the language bundle
     *
     * @see #load(Reader)
     */
    @NotNull
    public static FileLanguageBundle loadFromReader(@NotNull Reader reader) {
        Preconditions.checkArgument(reader != null, "reader must not be null");

        FileLanguageBundle language = new FileLanguageBundle();

        try {
            language.load(reader);
        } catch (FileNotFoundException e) { // We're going to ignore file not found exceptions
        } catch (IOException e) {
            e.printStackTrace();
        }

        return language;
    }

}
