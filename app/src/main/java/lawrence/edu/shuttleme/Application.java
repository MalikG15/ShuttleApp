package lawrence.edu.shuttleme;

/**
 * Created by David Jaglowski on 2/27/2017.
 */

import lawrence.edu.shuttleme.FontsOverride;

public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "DEFAULT", "SortsMillGoudy-Regular.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "SortsMillGoudy-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "SortsMillGoudy-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "SortsMillGoudy-Regular.ttf");
    }
}
