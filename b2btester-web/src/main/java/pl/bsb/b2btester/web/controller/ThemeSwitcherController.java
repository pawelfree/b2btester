package pl.bsb.b2btester.web.controller;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author aszatkowski
 */
@Named
@ApplicationScoped
public class ThemeSwitcherController implements Serializable {

    private String theme = "aristo";
    private String selectedTheme = "aristo";
    private Map<String, String> themes;

    public Map<String, String> getThemes() {
        return themes;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) { 
        this.theme = theme;
    }

    @PostConstruct
    public void init() {
        themes = new TreeMap<>();
        themes.put("Aristo", "aristo");
        themes.put("Blitzer", "blitzer");
        themes.put("Bluesky", "bluesky");
        themes.put("Casablanca", "casablanca");
        themes.put("Flick", "flick");
        themes.put("Glass-X", "glass-x");
        themes.put("Hot-Sneaks", "hot-sneaks");
        themes.put("Redmond", "redmond");
        themes.put("Rocket", "rocket");
        themes.put("Sam", "sam");
    }

    public void saveTheme() {   
        this.selectedTheme = theme;
    }
}
