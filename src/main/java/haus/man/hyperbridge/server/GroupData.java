package haus.man.hyperbridge.server;

import haus.man.hyperbridge.api.ILight;
import haus.man.hyperbridge.api.ILightGroup;
import lombok.Builder;
import ninja.bytecode.shuriken.collections.KList;

@Builder
public class GroupData {
    private String name;
    private String id;
    private KList<String> ids;
    private double watts;
    private double wattHours;

    public static GroupData from(ILightGroup l)
    {
        return GroupData.builder()
                .ids(l.getLights().convert(ILight::getId))
                .watts(l.getWattage())
                .wattHours(l.getTotalWattHours())
                .id(l.getId())
                .name(l.getName())
                .build();
    }
}
