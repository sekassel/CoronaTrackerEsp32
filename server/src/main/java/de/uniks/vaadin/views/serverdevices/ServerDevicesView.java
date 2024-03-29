package de.uniks.vaadin.views.serverdevices;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.uniks.Main;
import de.uniks.SpringBoot;
import de.uniks.cwa.CwaDataInterpreter;
import de.uniks.postgres.db.utils.UserPostgreSql;
import de.uniks.vaadin.views.main.MainView;
import de.uniks.vaadin.views.viewmodels.RsinEntrys;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Route(value = "serverDevicesInformation", layout = MainView.class)
@PageTitle("Server Devices")
@CssImport("./styles/views/serverdevices/server-devices-view.css")
public class ServerDevicesView extends VerticalLayout {
    private static final Logger LOG = Logger.getLogger(ServerDevicesView.class.getName());
    private static final UserPostgreSql userPostgreSql = new UserPostgreSql();

    public ServerDevicesView() {
        setId("server-devices-view");

        TextField trackerUserField = new TextField();
        trackerUserField.setValue(userPostgreSql.getUserCount().toString());
        trackerUserField.setLabel("ESP32 Tracker Users");
        trackerUserField.setWidth("150px");
        trackerUserField.setReadOnly(true);

        TextField infectionCheckField = new TextField();
        infectionCheckField.setValue(CwaDataInterpreter.lastCheckTimeString);
        infectionCheckField.setLabel("Last Infection Check Status");
        infectionCheckField.setWidth("350px");
        infectionCheckField.setReadOnly(true);

        TextField infectionCheckTimeField = new TextField();
        infectionCheckTimeField.setValue((int) CwaDataInterpreter.stopWatch.getTotalTimeSeconds() / 60 + " min");
        infectionCheckTimeField.setLabel("Total time needed");
        infectionCheckTimeField.setWidth("120px");
        infectionCheckTimeField.setReadOnly(true);

        Button manualInfStartButton = new Button();
        Button manualSmallInfStartButton = new Button();

        manualInfStartButton.setText("Infection Check");
        manualInfStartButton.setWidth("200px");
        manualInfStartButton.setDisableOnClick(true);
        manualInfStartButton.addClickListener(buttonClickEvent -> {
            manualSmallInfStartButton.setEnabled(false);
            if (Main.triggerInfectionCheck()) {
                Notification.show("Success! Restarted Infection Check Scheduler.");
            } else {
                Notification.show("Fail! Scheduler seems to be busy, try again later.");
            }
        });

        manualSmallInfStartButton.setText("Small Infection Check");
        manualSmallInfStartButton.setWidth("200px");
        manualSmallInfStartButton.setDisableOnClick(true);
        manualSmallInfStartButton.addClickListener(buttonClickEvent -> {
            manualInfStartButton.setEnabled(false);
            if (Main.triggerSmallInfectionCheck()) {
                Notification.show("Success! Restarted Infection Check Scheduler.");
            } else {
                Notification.show("Fail! Scheduler seems to be busy, try again later.");
            }
        });

        Details component = new Details();
        component.setSummaryText("Show Uni Tracker collected Information");
        component.addContent(trackerUserField);
        component.addContent(infectionCheckField);
        component.addContent(infectionCheckTimeField);
        component.addContent(manualInfStartButton);
        component.addContent(manualSmallInfStartButton);
        component.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
        component.setOpened(true);
        add(component);

        List<RsinEntrys> localInfectionList = new ArrayList<>();
        CwaDataInterpreter.getInfectedUserList().forEach(infectedUser -> {
            String rsin = infectedUser.getRsin().toString();
            List<byte[]> tekList = List.of(infectedUser.getTek());
            localInfectionList.add(new RsinEntrys(rsin, tekList));
        });

        if (localInfectionList.isEmpty()) {
            ProgressBar progressBar = new ProgressBar();
            progressBar.setIndeterminate(true);

            Details idleComp = new Details();
            idleComp.setSummaryText("No Infections detected. If there are infections detected, they will be shown here.");
            idleComp.addContent(progressBar);
            idleComp.setEnabled(false);
            idleComp.setOpened(true);
            idleComp.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);

            add(idleComp);
        } else {
            try {
                TreeGrid<RsinEntrys> grid = new TreeGrid<>();
                grid.setItems(localInfectionList);
                grid.addColumn(RsinEntrys::getRsinDate).setHeader("Date").setSortable(true);
                grid.addColumn(RsinEntrys::getRsin).setHeader("RSIN");
                grid.addColumn(RsinEntrys::getTekEntriesCount).setHeader("TEK Entrys");
                grid.setHeightByRows(true);
                grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                        GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
                grid.setSelectionMode(Grid.SelectionMode.NONE);
                grid.setItemDetailsRenderer(TemplateRenderer.<RsinEntrys>of(
                        "<div style='border: 1px solid gray; padding: 5px; width: 90%; box-sizing: border-box;'>"
                                + "<div><b>TEK Data: </b>[[item.tekAsBlock]]</div>"
                                + "</div>")
                        .withProperty("tekAsBlock", RsinEntrys::getTekListAsBlock)
                        .withEventHandler("handleClick", rsin -> {
                            grid.getDataProvider().refreshItem(rsin);
                        }));
                add(grid);
            } catch (Exception e) {
                // Constructor can throw exception for adding same item multiple times and
                // the dataprovider for grids behave sometimes a bit weird
                LOG.log(Level.WARNING, "Failed to initiate grid for Server Device View!", e);
                Notification.show("Ups, something went wrong with the Grid. Try again later!");
            }
        }
    }
}
