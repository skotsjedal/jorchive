package no.skotsj.jorchive.web.controller;

import com.google.common.collect.Lists;
import com.google.common.math.IntMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.math.DoubleMath.mean;

/**
 * draw icons from ascii art.
 * Inspired by Charles Parnot, http://cocoamine.net/blog/2015/03/20/replacing-photoshop-with-nsstring/
 * Created by Skotsj on 22.03.2015.
 */
@Controller
public class ImageController
{

    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    public static final String lock = "" +
            " · · · · · · · · · · · · · · · · " +
            " · · · · 1 · · · · · · 1 · · · · " +
            " · · · · · · · · · · · · · · · · " +
            " · · · · · · · · · · · · · · · · " +
            " · · · · · · · · · · · · · · · · " +
            " · · 3 · · · · · · · · · · 4 · · " +
            " · · · · · · · · · · · · · · · · " +
            " · · · · · · A A A A · · · · · · " +
            " · · · · 1 · A · · A · 1 · · · · " +
            " · · · · · · A C D A · · · · · · " +
            " · · · · · · A A A A · · · · · · " +
            " · · · · · · · · · · · · · · · · " +
            " · · · · · · · B E · · · · · · · " +
            " · · · · · · · · · · · · · · · · " +
            " · · 6 · · · · · · · · · · 5 · · " +
            " · · · · · · · · · · · · · · · · ";

    public static final String cross = "" +
            "· · · · 1 1 1 · · · ·" +
            "· · 1 · · · · · 1 · ·" +
            "· 1 · · · · · · · 1 ·" +
            "1 · · 2 · · · 3 · · 1" +
            "1 · · · # · # · · · 1" +
            "1 · · · · # · · · · 1" +
            "1 · · · # · # · · · 1" +
            "1 · · 3 · · · 2 · · 1" +
            "· 1 · · · · · · · 1 ·" +
            "· · 1 · · · · · 1 · ·" +
            "· · · · 1 1 1 · · · ·";

    Map<String, String> figures = new HashMap<>();

    public ImageController()
    {
        figures.put("cross", cross);
        figures.put("lock", lock);
    }

    @RequestMapping(value = "/img/{fig}/{size}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<byte[]> image(@PathVariable("fig") String fig, @PathVariable("size") int size) throws IOException
    {
        int[] charArray = figures.get(fig).chars().filter(c -> c != 32).toArray();
        int rowLen = IntMath.sqrt(charArray.length, RoundingMode.UNNECESSARY);

        log.info("size {}", size);
        int scale = size / (rowLen - 1);
        size = (rowLen - 1) * scale;
        log.info("size {}", size);

        HashMap<Character, ArrayList<Integer[]>> data = new HashMap<>();
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        g.setStroke(new BasicStroke(scale));

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size, size);

        log.info("rowLen {}", rowLen);
        HashMap<Shape, Color> shapes = new LinkedHashMap<>();
        for (int i = 0; i < charArray.length; i++)
        {
            char c = (char) charArray[i];
            ArrayList<Integer[]> list = data.getOrDefault(c, new ArrayList<>());
            list.add(new Integer[]{i % rowLen, i / rowLen});
            data.put(c, list);
        }

        ArrayList<Character> keys = Lists.newArrayList(data.keySet());
        Collections.sort(keys);

        Character last = null;
        log.info("scale {}", scale);
        List<Integer[]> polygonPoints = new ArrayList<>();
        for (Character character : keys)
        {
            if (!Character.isLetterOrDigit(character))
            {
                continue;
            }
            ArrayList<Integer[]> ints = data.get(character);
            int points = ints.size();

            // lines and ovals
            if (points > 1)
            {
                // finish previous polygon
                if (!polygonPoints.isEmpty())
                {
                    drawpolygon(g, polygonPoints, shapes);
                    polygonPoints.clear();
                }

                int[] xs = flatten(ints, 0);
                int[] ys = flatten(ints, 1);
                if (points == 2)
                {
                    drawLine(scale, g, shapes, xs, ys);
                } else
                {
                    drawOval(scale, g, shapes, points, xs, ys);
                }
            }
            // Polygons
            else
            {
                int x = ints.get(0)[0] * scale;
                int y = ints.get(0)[1] * scale;
                if (last == null || last == character - 1)
                {
                    polygonPoints.add(new Integer[]{x, y});
                } else
                {
                    if (!polygonPoints.isEmpty())
                    {
                        drawpolygon(g, polygonPoints, shapes);
                        polygonPoints.clear();
                    }
                    polygonPoints.add(new Integer[]{x, y});
                }
                last = character;
            }
        }
        if (polygonPoints.size() > 1)
        {
            drawpolygon(g, polygonPoints, shapes);
        }

        //drawBorder(size, g);
        //drawGrid(rowLen, scale, g);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(img, "png", stream);
        return new ResponseEntity<>(stream.toByteArray(), headers, HttpStatus.CREATED);
    }

    private void drawOval(int scale, Graphics2D g, HashMap<Shape, Color> shapes, int points, int[] xs, int[] ys)
    {
        int xm = (int) (mean(xs) * scale);
        int ym = (int) (mean(ys) * scale);
        int xr = div(xs) * scale;
        int yr = div(ys) * scale;
        Ellipse2D.Double oval = new Ellipse2D.Double(xm - xr / 2, ym - yr / 2, xr, yr);
        g.setColor(Color.BLACK);
        setColor(g, shapes, oval);
        shapes.put(oval, g.getColor());

        // > 4 points, filled oval
        if (points > 4)
        {
            g.fill(oval);
        }
        // not filled oval
        else
        {
            g.draw(oval);
        }
    }

    private void drawpolygon(Graphics2D g, List<Integer[]> current, HashMap<Shape, Color> shapes)
    {
        int[] xs = flatten(current, 0);
        int[] ys = flatten(current, 1);
        Polygon polygon = new Polygon(xs, ys, xs.length);
        setColor(g, shapes, polygon);
        shapes.put(polygon, g.getColor());
        g.fillPolygon(polygon);
    }

    private void drawLine(int scale, Graphics2D g, HashMap<Shape, Color> shapes, int[] xs, int[] ys)
    {
        Line2D.Double line = new Line2D.Double(scale * xs[0], scale * ys[0], scale * xs[1], scale * ys[1]);
        setColor(g, shapes, line);
        g.draw(line);
    }

    private void setColor(Graphics2D g, HashMap<Shape, Color> shapes, Shape shape)
    {
        g.setColor(Color.BLACK);
        shapes.keySet().stream().filter(p -> p.contains(shape.getBounds().getCenterX(), shape.getBounds().getCenterY()))
                .forEach(p -> g.setColor(Color.WHITE));
    }

    private void drawGrid(int rowLen, int scale, Graphics2D g)
    {
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.RED);
        for (int i = 0; i < rowLen; i++)
        {
            for (int j = 0; j < rowLen; j++)
            {
                g.drawOval(-1 + i * scale, -1 + j * scale, 1, 1);
            }
        }
    }

    private void drawBorder(int size, Graphics2D g)
    {
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.BLUE);
        int sizem = size - 1;
        g.drawLine(0, 0, 0, sizem);
        g.drawLine(0, 0, sizem, 0);
        g.drawLine(sizem, 0, sizem, sizem);
        g.drawLine(0, sizem, sizem, sizem);
    }

    private int div(int[] xs)
    {
        return max(xs) - min(xs);
    }

    private int max(int[] xs)
    {
        int max = xs[0];
        for (int i = 1; i < xs.length; i++)
        {
            if (xs[i] > max)
            {
                max = xs[i];
            }
        }
        return max;
    }

    private int min(int[] xs)
    {
        int min = xs[0];
        for (int i = 1; i < xs.length; i++)
        {
            if (xs[i] < min)
            {
                min = xs[i];
            }
        }
        return min;
    }

    private int[] flatten(List<Integer[]> list, int i)
    {
        int[] array = new int[list.size()];
        for (int i1 = 0; i1 < list.size(); i1++)
        {
            array[i1] = list.get(i1)[i];
        }
        return array;
    }
}
