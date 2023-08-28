
/**
 * Enum a korongok színéhez
 */
public enum ManColor {
    PinkPanther,
    YellowStone,
    WhiteHouse;

    @Override
    public String toString() {
        return switch (this) {
            case PinkPanther -> "PinkPanther";
            case YellowStone -> "YellowStone";
            case WhiteHouse -> "WhiteHouse";
        };
    }
}
