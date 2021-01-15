package haus.man.hyperbridge.server;

import haus.man.hyperbridge.api.ILight;
import lombok.Builder;

@Builder
public class LightData {
    private String name;
    private String id;
    private int r;
    private int g;
    private int b;
    private int a;
    private double watts;
    private double wattHours;

    public static LightData from(ILight l)
    {
        return LightData.builder()
                .r(l.getColor().getRed())
                .g(l.getColor().getGreen())
                .b(l.getColor().getBlue())
                .a((int) (l.getBrightness() * 255))
                .watts(l.getWattage())
                .wattHours(l.getTotalWattHours())
                .id(l.getId())
                .name(l.getName())
                .build();
    }
}
