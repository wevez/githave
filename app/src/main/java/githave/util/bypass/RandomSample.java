package githave.util.bypass;

import githave.MCHook;
import githave.manager.rotation.RotationManager;
import net.minecraft.util.MathHelper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class RandomSample implements MCHook {

    public final float[] samepl;
    public final float yaw;


    private RandomSample(float yaw, float[] samepl) {
        this.samepl = samepl;
        this.yaw = yaw;
    }

    public static final List<RandomSample> SAMPLE = Arrays.asList(
            new RandomSample(-269.873047f, new float[] { -1.176819f, 1.349973f }),
            new RandomSample(-269.872833f, new float[] { -1.177032f, 1.349973f }),
            new RandomSample(-269.872833f, new float[] { -1.177032f, 1.349973f }),
            new RandomSample(-269.872833f, new float[] { -1.177032f, 1.349973f }),
            new RandomSample(88.856544f, new float[] { 0.093597f, 0.749973f }),
            new RandomSample(86.778374f, new float[] { 2.021759f, -0.450027f }),
            new RandomSample(83.684410f, new float[] { 2.865753f, -0.150027f }),
            new RandomSample(80.037483f, new float[] { 1.262695f, -0.150027f }),
            new RandomSample(76.107994f, new float[] { -2.607849f, -0.300027f }),
            new RandomSample(72.031395f, new float[] { -3.631226f, -0.300027f }),
            new RandomSample(67.875374f, new float[] { -3.675201f, -0.300027f }),
            new RandomSample(63.677460f, new float[] { -4.277313f, -0.300027f }),
            new RandomSample(59.454491f, new float[] { -4.554352f, -0.300027f }),
            new RandomSample(55.210373f, new float[] { -3.010254f, -0.300027f }),
            new RandomSample(50.955532f, new float[] { -2.205444f, -0.300027f }),
            new RandomSample(46.700085f, new float[] { -2.899994f, -0.300027f }),
            new RandomSample(42.443508f, new float[] { -1.943481f, -0.150027f }),
            new RandomSample(38.190475f, new float[] { -2.040466f, 0.149973f }),
            new RandomSample(33.941273f, new float[] { -0.641327f, 0.299973f }),
            new RandomSample(29.702538f, new float[] { -1.202606f, 0.299973f }),
            new RandomSample(25.474588f, new float[] { -1.624634f, 0.299973f }),
            new RandomSample(21.258368f, new float[] { -3.558411f, 0.149973f }),
            new RandomSample(17.042267f, new float[] { -2.492310f, 0.149973f }),
            new RandomSample(12.827188f, new float[] { -2.477264f, 0.149973f }),
            new RandomSample(8.612446f, new float[] { -1.262604f, 0.149973f }),
            new RandomSample(4.403323f, new float[] { -0.653503f, 0.149973f }),
            new RandomSample(0.204424f, new float[] { 0.095367f, 0.149973f }),
            new RandomSample(-3.395786f, new float[] { -2.154388f, 0.149973f }),
            new RandomSample(-6.722343f, new float[] { -4.977783f, 0.149973f }),
            new RandomSample(-9.975494f, new float[] { -6.074646f, 0.599973f }),
            new RandomSample(-14.077619f, new float[] { -4.222565f, 1.049973f }),
            new RandomSample(-18.646269f, new float[] { -3.553955f, 1.799973f }),
            new RandomSample(-23.453669f, new float[] { -2.946564f, 2.249973f }),
            new RandomSample(-28.375067f, new float[] { -1.325195f, 2.549973f }),
            new RandomSample(-33.337296f, new float[] { 0.036987f, 2.699974f }),
            new RandomSample(-38.298378f, new float[] { -0.551910f, 2.699974f }),
            new RandomSample(-43.243839f, new float[] { 0.343536f, 2.699974f }),
            new RandomSample(-48.162922f, new float[] { 1.512604f, 2.549973f }),
            new RandomSample(-53.046997f, new float[] { 1.446655f, 2.549973f }),
            new RandomSample(-57.893799f, new float[] { 1.793457f, 2.549973f }),
            new RandomSample(-62.702271f, new float[] { 1.801941f, 2.399973f }),
            new RandomSample(-67.473068f, new float[] { 1.772766f, 2.399973f }),
            new RandomSample(-71.651871f, new float[] { 1.751526f, 2.399973f }),
            new RandomSample(-75.632637f, new float[] { 0.332306f, 2.399973f }),
            new RandomSample(-79.612076f, new float[] { -0.788269f, 2.549973f }),
            new RandomSample(-83.625549f, new float[] { -4.124817f, 2.699974f }),
            new RandomSample(-88.686539f, new float[] { -6.413818f, 2.849974f }),
            new RandomSample(-94.337128f, new float[] { -5.413208f, 2.999974f }),
            new RandomSample(-100.297050f, new float[] { -3.503296f, 2.999974f }),
            new RandomSample(-106.396263f, new float[] { -1.604126f, 3.299974f }),
            new RandomSample(-112.532188f, new float[] { 1.231781f, 3.599974f }),
            new RandomSample(-118.629921f, new float[] { 4.029480f, 3.599974f }),
            new RandomSample(-124.633469f, new float[] { 6.583008f, 3.299974f }),
            new RandomSample(-130.513550f, new float[] { 7.213104f, 3.299974f }),
            new RandomSample(-136.253510f, new float[] { 9.053070f, 2.999974f }),
            new RandomSample(-141.856644f, new float[] { 8.506226f, 2.999974f }),
            new RandomSample(-147.351166f, new float[] { 5.000732f, 2.999974f }),
            new RandomSample(-152.769257f, new float[] { 0.518829f, 2.999974f }),
            new RandomSample(-158.144012f, new float[] { 0.943512f, 2.999974f }),
            new RandomSample(-163.481644f, new float[] { 2.681091f, 2.699974f }),
            new RandomSample(-168.770065f, new float[] { 4.369629f, 2.699974f }),
            new RandomSample(-173.995468f, new float[] { 5.094971f, 2.699974f }),
            new RandomSample(-178.644104f, new float[] { 5.093628f, 2.699974f }),
            new RandomSample(-183.230148f, new float[] { 7.129700f, 2.999974f }),
            new RandomSample(-187.985428f, new float[] { 8.734985f, 3.599974f }),
            new RandomSample(-193.039520f, new float[] { 10.489105f, 4.499975f }),
            new RandomSample(-198.832474f, new float[] { 9.832031f, 5.699975f }),
            new RandomSample(-204.891891f, new float[] { 8.241516f, 6.899976f }),
            new RandomSample(-210.984055f, new float[] { 6.533752f, 7.799977f }),
            new RandomSample(-217.011078f, new float[] { 6.260712f, 8.249976f }),
            new RandomSample(-222.941147f, new float[] { 5.440674f, 8.249976f }),
            new RandomSample(-228.759201f, new float[] { 7.358765f, 8.249976f }),
            new RandomSample(-234.448074f, new float[] { 8.997620f, 7.799976f }),
            new RandomSample(-239.982086f, new float[] { 11.831635f, 7.199976f }),
            new RandomSample(-245.346634f, new float[] { 13.296204f, 5.849976f }),
            new RandomSample(-250.559891f, new float[] { 12.209473f, 3.899975f }),
            new RandomSample(-255.649216f, new float[] { 10.548767f, 2.999974f }),
            new RandomSample(-260.640839f, new float[] { 8.640350f, 2.699974f }),
            new RandomSample(-265.553772f, new float[] { 7.253296f, 2.549974f }),
            new RandomSample(89.605713f, new float[] { 7.593750f, 2.399974f }),
            new RandomSample(84.846893f, new float[] { 9.502686f, 2.249974f }),
            new RandomSample(80.172577f, new float[] { 9.526978f, 2.249974f }),
            new RandomSample(76.464256f, new float[] { 10.685303f, 2.249974f }),
            new RandomSample(73.398170f, new float[] { 10.451355f, 2.249974f }),
            new RandomSample(72.124008f, new float[] { 7.975586f, 2.249974f }),
            new RandomSample(71.642990f, new float[] { 4.856628f, 2.549974f }),
            new RandomSample(71.507301f, new float[] { 3.492310f, 2.549974f })
    );

    public static float[] pickSample(float yaw) {
        return SAMPLE.stream().min(Comparator.comparingDouble(c -> Math.abs(MathHelper.wrapAngleTo180_double(mc.thePlayer.rotationYaw - c.yaw)))).get().samepl;
    }
}
