package de.oppermann.maven.pflist.checker;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.defect.PropertyNotDefinedDefect;
import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFProperty;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class CheckPropertyExists implements PFListCheck {

    private File propertyFile;

    public CheckPropertyExists(File propertyFile) {
        this.propertyFile = propertyFile;
    }

    public List<Defect> checkForErrors(PFList pfList) {
        List<Defect> defects = new ArrayList<Defect>();
        List<PFProperty> pfProperties = pfList.getPfProperties();
        Properties properties = getProperties();
        for (PFProperty pfProperty : pfProperties) {
            if (properties.containsKey(pfProperty.getId()))
                continue;
            Defect defect = new PropertyNotDefinedDefect(pfList, pfProperty, propertyFile);
            defects.add(defect);
        }
        return defects;
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(propertyFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

}