package core;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Convert image object to bytecode or get from bytecode.
 * Provided methods to convert image to any resolution rate or color area.
 *
 * @author Rock Chin
 */
public class ImageConvert {
    private BufferedImage origin;
    private BufferedImage product;

    public BufferedImage getOriginImage() {
        return origin;
    }

    public void setOriginImage(BufferedImage origin) {
        this.origin = origin;
    }

    public BufferedImage getProduct() {
        return product;
    }

    public void setProduct(BufferedImage product) {
        this.product = product;
    }

    public ImageConvert(BufferedImage origin) {
        this.origin = origin;
    }

    /**
     * change resolution rate
     *
     * @param rate rate on width OR height
     * @return this
     */
    public ImageConvert changeResolutionRate(double rate) {
        Image img = origin.getScaledInstance((int) (origin.getWidth() * rate), (int) (origin.getHeight() * rate), Image.SCALE_DEFAULT);
        product = new BufferedImage((int) (origin.getWidth() * rate), (int) (origin.getHeight() * rate)
                , BufferedImage.TYPE_INT_ARGB);
        product.createGraphics().drawImage(img, 0, 0, null);
//		if(rate>0.9999&&rate<1.001){
//			product=new BufferedImage(origin.getWidth(),origin.getHeight(),BufferedImage.TYPE_INT_ARGB);
//			product.setRGB(0,0,product.getWidth(),product.getHeight(),
//					origin.getRGB(0,0,origin.getWidth(),origin.getHeight(),null,0,origin.getWidth())
//					,0,product.getWidth());
//			return this;
//		}
//		if (rate<=0){
//			throw new IllegalArgumentException("rate should >0");
//		}
//		product=new BufferedImage((int)(origin.getWidth()*rate),(int)(origin.getHeight()*rate)
//				,BufferedImage.TYPE_INT_ARGB);
//		double step=1.0/rate;
//		int x=0,y=0;//product的指针
//		for(double i=0;i<origin.getHeight();i+=step){
//			for(double j=0;j<origin.getWidth();j+=step){
//				if(x<product.getWidth()&&y<product.getHeight()){
//					product.setRGB(x++,y,origin.getRGB((int)j,(int)i));
//				}
//			}
//			y++;
//			x=0;
//		}
//		System.out.println(product.getWidth()+" "+product.getHeight()+" "+origin.getWidth()+" "+origin.getHeight());
        return this;
    }

    /**
     * change color space
     *
     * @param rate color space(e.g. 0.72)
     * @return this
     */
    public ImageConvert changeColorSpace(double rate) {
        if (rate > 0.9999 && rate < 1.001) {
            product = new BufferedImage(origin.getWidth(), origin.getHeight(), BufferedImage.TYPE_INT_ARGB);
            product.setRGB(0, 0, product.getWidth(), product.getHeight(),
                    origin.getRGB(0, 0, origin.getWidth(), origin.getHeight(), null, 0, origin.getWidth())
                    , 0, product.getWidth());
            return this;
        }
        if (rate > 1) {
            throw new IllegalArgumentException("rate should <=1");
        }
//		System.out.println("step:"+(int)(1.0/rate));
        product = new BufferedImage(origin.getWidth(), origin.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < origin.getHeight(); i++) {
            for (int j = 0; j < origin.getWidth(); j++) {
                product.setRGB(j, i, limitColor(origin.getRGB(j, i), (int) (1.0 / rate)));
            }
        }
        return this;
    }

    /**
     *
     *            图片
     * @param angel
     *            旋转角度
     * @return
     */
    public ImageConvert rotateImage(int angel) {
        BufferedImage bufferedImage= origin;
        if (bufferedImage == null) {
            return null;
        }
        if (angel < 0) {
            // 将负数角度，纠正为正数角度
            angel = angel + 360;
        }
        int imageWidth = bufferedImage.getWidth(null);
        int imageHeight = bufferedImage.getHeight(null);
        // 计算重新绘制图片的尺寸
        Rectangle rectangle = calculatorRotatedSize(new Rectangle(new Dimension(imageWidth, imageHeight)), angel);
        // 获取原始图片的透明度
        int type = bufferedImage.getColorModel().getTransparency();
        BufferedImage newImage = null;
        newImage = new BufferedImage(rectangle.width, rectangle.height, type);
        Graphics2D graphics = newImage.createGraphics();
        // 平移位置
        graphics.translate((rectangle.width - imageWidth) / 2, (rectangle.height - imageHeight) / 2);
        // 旋转角度
        graphics.rotate(Math.toRadians(angel), imageWidth / 2, imageHeight / 2);
        // 绘图
        graphics.drawImage(bufferedImage, null, null);

        this.product = newImage;

        return this;
    }
    private static Rectangle calculatorRotatedSize(Rectangle src, int angel) {
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }
        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) src.height / src.width);
        double angel_dalta_height = Math.atan((double) src.width / src.height);

        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
        int des_width = src.width + len_dalta_width * 2;
        int des_height = src.height + len_dalta_height * 2;
        return new Rectangle(new Dimension(des_width, des_height));
    }


    private int limitColor(int rgb, int step) {
        Color before = new Color(rgb);
        Color result = new Color(before.getRed() - before.getRed() % step, before.getGreen() - before.getGreen() % step, before.getBlue() - before.getBlue() % step);
        return result.getRGB();
    }

    public void cutImage(int x, int y, int w, int h) {
        product = product.getSubimage(x, y, w, h);
    }

}
