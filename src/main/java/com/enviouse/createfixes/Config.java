package com.enviouse.createfixes;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Createfixes.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // ---- Factory Panel ---------------------------------------------------
    private static final ForgeConfigSpec.BooleanValue ENABLE_FACTORY_PANEL_THROTTLE;
    private static final ForgeConfigSpec.IntValue FACTORY_PANEL_TICK_SKIP;

    // ---- Smart Observer --------------------------------------------------
    private static final ForgeConfigSpec.BooleanValue ENABLE_SMART_OBSERVER_THROTTLE;
    private static final ForgeConfigSpec.IntValue SMART_OBSERVER_TICK_SKIP;

    // ---- Chute -----------------------------------------------------------
    private static final ForgeConfigSpec.BooleanValue ENABLE_CHUTE_IDLE_THROTTLE;
    private static final ForgeConfigSpec.IntValue CHUTE_IDLE_TICK_SKIP;

    // ---- Funnel ----------------------------------------------------------
    private static final ForgeConfigSpec.BooleanValue ENABLE_FUNNEL_THROTTLE;
    private static final ForgeConfigSpec.IntValue FUNNEL_TICK_SKIP;

    // ---- Schematicannon --------------------------------------------------
    private static final ForgeConfigSpec.BooleanValue ENABLE_SCHEMATICANNON_THROTTLE;
    private static final ForgeConfigSpec.IntValue SCHEMATICANNON_TICK_SKIP;

    // ---- Block Breaker (drill/saw base) ----------------------------------
    private static final ForgeConfigSpec.BooleanValue ENABLE_BLOCK_BREAKER_THROTTLE;
    private static final ForgeConfigSpec.IntValue BLOCK_BREAKER_TICK_SKIP;

    // ---- Mechanical Crafter ----------------------------------------------
    private static final ForgeConfigSpec.BooleanValue ENABLE_MECHANICAL_CRAFTER_THROTTLE;
    private static final ForgeConfigSpec.IntValue MECHANICAL_CRAFTER_TICK_SKIP;

    // ---- Mechanical Arm --------------------------------------------------
    private static final ForgeConfigSpec.BooleanValue ENABLE_ARM_THROTTLE;
    private static final ForgeConfigSpec.IntValue ARM_TICK_SKIP;

    // ---- Packager --------------------------------------------------------
    private static final ForgeConfigSpec.BooleanValue ENABLE_PACKAGER_THROTTLE;
    private static final ForgeConfigSpec.IntValue PACKAGER_TICK_SKIP;

    // ---- Frogport --------------------------------------------------------
    private static final ForgeConfigSpec.BooleanValue ENABLE_FROGPORT_THROTTLE;
    private static final ForgeConfigSpec.IntValue FROGPORT_TICK_SKIP;

    // ---- Hose Pulley -----------------------------------------------------
    private static final ForgeConfigSpec.BooleanValue ENABLE_HOSE_PULLEY_THROTTLE;
    private static final ForgeConfigSpec.IntValue HOSE_PULLEY_TICK_SKIP;

    // ---- Mekanism (optional, off by default) -----------------------------
    private static final ForgeConfigSpec.BooleanValue ENABLE_MEKANISM_THROTTLE;
    private static final ForgeConfigSpec.IntValue MEKANISM_TICK_SKIP;

    static {
        BUILDER.push("factoryPanel");
        ENABLE_FACTORY_PANEL_THROTTLE = BUILDER
                .comment("Throttle Factory Panel behaviour server-tick. Their heavy request path is already gated by",
                        "Create's factoryGaugeTimer; this additionally skips the per-tick storage-monitor work.")
                .define("enable", true);
        FACTORY_PANEL_TICK_SKIP = BUILDER
                .comment("Run Factory Panel server tick once every N ticks. 1 = no throttle.")
                .defineInRange("tickSkip", 10, 1, 200);
        BUILDER.pop();

        BUILDER.push("smartObserver");
        ENABLE_SMART_OBSERVER_THROTTLE = BUILDER
                .comment("Throttle Smart Observer server tick. Observers normally re-scan their target every tick;",
                        "items on belts move slowly enough that checking every few ticks is visually identical.")
                .define("enable", true);
        SMART_OBSERVER_TICK_SKIP = BUILDER
                .comment("Run Smart Observer server tick once every N ticks. 1 = no throttle.",
                        "Higher values cut per-observer cost linearly but add up to N ticks of detection latency.")
                .defineInRange("tickSkip", 3, 1, 20);
        BUILDER.pop();

        BUILDER.push("chute");
        ENABLE_CHUTE_IDLE_THROTTLE = BUILDER
                .comment("Throttle idle (empty) chute server tick. When a chute holds no item it polls the inventory",
                        "above every tick; this is safe to slow down since the source inventory change tracker",
                        "(VersionedInventoryTrackerBehaviour) already short-circuits unchanged sources anyway.")
                .define("enable", true);
        CHUTE_IDLE_TICK_SKIP = BUILDER
                .comment("Run idle chute server tick once every N ticks. Only applies while the chute holds no item.",
                        "Chutes currently holding or moving an item always tick every tick.")
                .defineInRange("tickSkip", 4, 1, 20);
        BUILDER.pop();

        BUILDER.push("funnel");
        ENABLE_FUNNEL_THROTTLE = BUILDER
                .comment("Throttle Funnel server tick. Funnels self-gate via extractionCooldown and the inventory",
                        "version tracker; an additional tick-skip is safe but introduces up to N-tick latency before",
                        "the funnel notices a freshly-available item.")
                .define("enable", true);
        FUNNEL_TICK_SKIP = BUILDER
                .comment("Run Funnel server tick once every N ticks. 1 = no throttle.")
                .defineInRange("tickSkip", 3, 1, 20);
        BUILDER.pop();

        BUILDER.push("schematicannon");
        ENABLE_SCHEMATICANNON_THROTTLE = BUILDER
                .comment("Throttle Schematicannon server tick while STOPPED or PAUSED. Running cannons always tick",
                        "every tick so print speed is unchanged.")
                .define("enable", true);
        SCHEMATICANNON_TICK_SKIP = BUILDER
                .comment("Run stopped/paused Schematicannon server tick once every N ticks. 1 = no throttle.")
                .defineInRange("tickSkip", 10, 1, 100);
        BUILDER.pop();

        BUILDER.push("blockBreaker");
        ENABLE_BLOCK_BREAKER_THROTTLE = BUILDER
                .comment("Throttle BlockBreakingKineticBlockEntity (drill / mechanical saw base) while idle.",
                        "The BE already self-gates breaking via ticksUntilNextProgress; this skips the polling",
                        "tick when there is no target block. Active breaking is never throttled.")
                .define("enable", true);
        BLOCK_BREAKER_TICK_SKIP = BUILDER
                .comment("Run idle drill/saw tick once every N ticks. 1 = no throttle.")
                .defineInRange("tickSkip", 5, 1, 40);
        BUILDER.pop();

        BUILDER.push("mechanicalCrafter");
        ENABLE_MECHANICAL_CRAFTER_THROTTLE = BUILDER
                .comment("Throttle Mechanical Crafter server tick while its phase is IDLE (no recipe in flight).",
                        "During ACCEPTING / ASSEMBLING / EXPORTING / CRAFTING / WAITING / INSERTING, the crafter",
                        "ticks every tick so craft timing is preserved.")
                .define("enable", true);
        MECHANICAL_CRAFTER_TICK_SKIP = BUILDER
                .comment("Run idle Mechanical Crafter tick once every N ticks. 1 = no throttle.")
                .defineInRange("tickSkip", 5, 1, 40);
        BUILDER.pop();

        BUILDER.push("mechanicalArm");
        ENABLE_ARM_THROTTLE = BUILDER
                .comment("Throttle Mechanical Arm server tick while the arm is parked in SEARCH_INPUTS or",
                        "SEARCH_OUTPUTS with no active movement. Any arm in motion, holding an item, or dancing",
                        "ticks every tick so transfers and animations stay smooth.")
                .define("enable", true);
        ARM_TICK_SKIP = BUILDER
                .comment("Run parked Arm tick once every N ticks. 1 = no throttle.",
                        "Higher values add latency before the arm notices new items in its inputs.")
                .defineInRange("tickSkip", 4, 1, 20);
        BUILDER.pop();

        BUILDER.push("packager");
        ENABLE_PACKAGER_THROTTLE = BUILDER
                .comment("Throttle Packager server tick while fully idle (no animation, no held box, no queued",
                        "exits, no button cooldown). Active packaging always ticks every tick.")
                .define("enable", true);
        PACKAGER_TICK_SKIP = BUILDER
                .comment("Run idle Packager tick once every N ticks. 1 = no throttle.")
                .defineInRange("tickSkip", 4, 1, 20);
        BUILDER.pop();

        BUILDER.push("frogport");
        ENABLE_FROGPORT_THROTTLE = BUILDER
                .comment("Throttle Frogport server tick while no animation is in progress. Frogports search for",
                        "work through their lazyTick (every 10 ticks by default), so skipping the main tick when",
                        "idle is safe.")
                .define("enable", true);
        FROGPORT_TICK_SKIP = BUILDER
                .comment("Run idle Frogport tick once every N ticks. 1 = no throttle.")
                .defineInRange("tickSkip", 4, 1, 20);
        BUILDER.pop();

        BUILDER.push("hosePulley");
        ENABLE_HOSE_PULLEY_THROTTLE = BUILDER
                .comment("Throttle Hose Pulley server tick while parked (no kinetic speed and not extending /",
                        "retracting). Active pulleys tick every tick so fluid extraction rate is unchanged.")
                .define("enable", true);
        HOSE_PULLEY_TICK_SKIP = BUILDER
                .comment("Run parked Hose Pulley tick once every N ticks. 1 = no throttle.")
                .defineInRange("tickSkip", 4, 1, 20);
        BUILDER.pop();

        BUILDER.push("mekanism");
        ENABLE_MEKANISM_THROTTLE = BUILDER
                .comment("EXPERIMENTAL. Throttle TileEntityMekanism.tickServer across ALL Mekanism tiles (machines,",
                        "tanks, cables, generators). Skipping ticks proportionally slows energy / chemical / fluid",
                        "transfer and recipe progression — a skip of 2 roughly halves Mekanism throughput.",
                        "Leave disabled unless Mekanism tick cost is a confirmed TPS bottleneck on your server.")
                .define("enable", false);
        MEKANISM_TICK_SKIP = BUILDER
                .comment("Run TileEntityMekanism.tickServer once every N ticks. 1 = no throttle.",
                        "2 ≈ halved throughput, 3 ≈ one-third throughput, etc. Use values > 1 with care.")
                .defineInRange("tickSkip", 1, 1, 10);
        BUILDER.pop();
    }

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean enableFactoryPanelThrottle;
    public static int factoryPanelTickSkip;
    public static boolean enableSmartObserverThrottle;
    public static int smartObserverTickSkip;
    public static boolean enableChuteIdleThrottle;
    public static int chuteIdleTickSkip;
    public static boolean enableFunnelThrottle;
    public static int funnelTickSkip;
    public static boolean enableSchematicannonThrottle;
    public static int schematicannonTickSkip;
    public static boolean enableBlockBreakerThrottle;
    public static int blockBreakerTickSkip;
    public static boolean enableMechanicalCrafterThrottle;
    public static int mechanicalCrafterTickSkip;
    public static boolean enableArmThrottle;
    public static int armTickSkip;
    public static boolean enablePackagerThrottle;
    public static int packagerTickSkip;
    public static boolean enableFrogportThrottle;
    public static int frogportTickSkip;
    public static boolean enableHosePulleyThrottle;
    public static int hosePulleyTickSkip;
    public static boolean enableMekanismThrottle;
    public static int mekanismTickSkip;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) return;
        enableFactoryPanelThrottle = ENABLE_FACTORY_PANEL_THROTTLE.get();
        factoryPanelTickSkip = FACTORY_PANEL_TICK_SKIP.get();
        enableSmartObserverThrottle = ENABLE_SMART_OBSERVER_THROTTLE.get();
        smartObserverTickSkip = SMART_OBSERVER_TICK_SKIP.get();
        enableChuteIdleThrottle = ENABLE_CHUTE_IDLE_THROTTLE.get();
        chuteIdleTickSkip = CHUTE_IDLE_TICK_SKIP.get();
        enableFunnelThrottle = ENABLE_FUNNEL_THROTTLE.get();
        funnelTickSkip = FUNNEL_TICK_SKIP.get();
        enableSchematicannonThrottle = ENABLE_SCHEMATICANNON_THROTTLE.get();
        schematicannonTickSkip = SCHEMATICANNON_TICK_SKIP.get();
        enableBlockBreakerThrottle = ENABLE_BLOCK_BREAKER_THROTTLE.get();
        blockBreakerTickSkip = BLOCK_BREAKER_TICK_SKIP.get();
        enableMechanicalCrafterThrottle = ENABLE_MECHANICAL_CRAFTER_THROTTLE.get();
        mechanicalCrafterTickSkip = MECHANICAL_CRAFTER_TICK_SKIP.get();
        enableArmThrottle = ENABLE_ARM_THROTTLE.get();
        armTickSkip = ARM_TICK_SKIP.get();
        enablePackagerThrottle = ENABLE_PACKAGER_THROTTLE.get();
        packagerTickSkip = PACKAGER_TICK_SKIP.get();
        enableFrogportThrottle = ENABLE_FROGPORT_THROTTLE.get();
        frogportTickSkip = FROGPORT_TICK_SKIP.get();
        enableHosePulleyThrottle = ENABLE_HOSE_PULLEY_THROTTLE.get();
        hosePulleyTickSkip = HOSE_PULLEY_TICK_SKIP.get();
        enableMekanismThrottle = ENABLE_MEKANISM_THROTTLE.get();
        mekanismTickSkip = MEKANISM_TICK_SKIP.get();
    }
}
