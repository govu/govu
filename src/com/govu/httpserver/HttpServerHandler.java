package com.govu.httpserver;

import com.govu.Govu;
import com.govu.application.WebApplication;
import com.govu.engine.render.Renderer;
import com.govu.engine.render.exception.ControllerNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import javax.activation.MimetypesFileTypeMap;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelFutureProgressListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.DefaultFileRegion;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.FileRegion;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.*;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.*;
import org.jboss.netty.handler.codec.http.multipart.Attribute;
import org.jboss.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.jboss.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.jboss.netty.util.CharsetUtil;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeObject;

public class HttpServerHandler extends SimpleChannelHandler {

    public static Logger logger;

    public HttpServerHandler() {
        logger = Logger.getLogger("accessLog");
    }
    private HttpRequest request;
    private HttpResponse response;
    private ChannelFuture future;

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        request = (HttpRequest) e.getMessage();
        logger.info(ctx.getChannel().getRemoteAddress().toString() + ": " + request.getUri());
        String pathString = request.getUri();

        if (pathString.toLowerCase().startsWith("/controller/")
                || pathString.toLowerCase().startsWith("/model/")
                || pathString.toLowerCase().startsWith("/view/")) {
            response = new DefaultHttpResponse(HTTP_1_1, FORBIDDEN);
            response.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");
            response.setContent(ChannelBuffers.copiedBuffer("Forbidden", CharsetUtil.UTF_8));
            future = e.getChannel().write(response);
            future.addListener(ChannelFutureListener.CLOSE);
        } else {
            String host = request.getHeader("host");
            WebApplication app = Govu.getWebApp(host, pathString);
            File file = new File(Govu.webRoot + "/" + pathString);

            if (app == null) {
                if (file.exists()) {
                    writeFile(file, ctx, request, e);
                } else {
                    sendError(ctx, NOT_FOUND,"1");
                }
            } else {
                try {
                    render(app, pathString,ctx, e);
                } catch (ControllerNotFoundException ex) {
                    file = new File( app.getAbsolutePath()+ app.getRelativePath(pathString));
                    if (file.exists()) {
                        writeFile(file, ctx, request, e);
                    } else {
                        sendError(ctx, NOT_FOUND,"2");
                    }
                }
            }
        }

    }

    public void render(WebApplication app, String pathString,ChannelHandlerContext ctx, MessageEvent e) throws ControllerNotFoundException {
        String res = "";
        String redirect = null;
        Renderer renderer = null;
        try {
            String relativePath = app.getRelativePath(pathString);
            if (relativePath.equals("/")) {
                relativePath = "/index";
            }
            URIBuilder uri = new URIBuilder(relativePath);


            String[] path = uri.getPath().split("/");

            if (path.length > 1) {
                String type = path[1];
                String method = path.length > 2 ? path[2] : "index";

                HashMap<String, String> query = new HashMap<>();
                for (Iterator<NameValuePair> itr = uri.getQueryParams().iterator(); itr.hasNext();) {
                    NameValuePair pair = itr.next();
                    query.put(pair.getName(), pair.getValue());
                }
                if (request.getMethod() == HttpMethod.POST) {
                    HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
                    try {
                        while (decoder.hasNext()) {
                            InterfaceHttpData data = decoder.next();
                            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                                query.put(data.getName(), ((Attribute) data).getValue());
                            }
                        }
                    } catch (HttpPostRequestDecoder.EndOfDataDecoderException ex) {
                    }
                }

                Set<Cookie> cookies;
                String value = request.getHeader(HttpHeaders.Names.COOKIE);
                if (value == null) {
                    cookies = Collections.emptySet();
                } else {
                    CookieDecoder decoder = new CookieDecoder();
                    cookies = decoder.decode(value);
                }


                renderer = new Renderer(app,type, method, query, cookies);
                res = renderer.getResponse();

            } else {
                throw new ControllerNotFoundException(pathString);
            }

        } catch (EcmaError ex) {
            res = ex.getErrorMessage();
        } catch (JavaScriptException ex) {
            if (ex.getValue() != null) {
                NativeObject obj = (NativeObject) ex.getValue();
                if (obj.get("error").equals("redirect")) {
                    redirect = obj.get("path").toString();
                } else {
                    HttpResponseStatus httpRes = new HttpResponseStatus(500, obj.get("msg").toString());
                    sendError(ctx, httpRes,"0");
                }
            } else {
                res = ex.getMessage();
            }

        } catch (URISyntaxException ex) {
            Govu.logger.error("URISyntaxException", ex);
            res = ex.getMessage();
        } catch (HttpPostRequestDecoder.ErrorDataDecoderException ex) {
            Govu.logger.error("ErrorDataDecoderException", ex);
            res = ex.getMessage();
        } catch (HttpPostRequestDecoder.IncompatibleDataDecoderException ex) {
            Govu.logger.error("IncompatibleDataDecoderException", ex);
            res = ex.getMessage();
        } catch (FileNotFoundException ex) {
            Govu.logger.error("FileNotFoundException", ex);
            res = "View not found: " + request.getUri();
        } catch (EvaluatorException ex) {
            Govu.logger.error("EvaluatorException", ex);
            res = "Syntax error: " + ex.getMessage();
        } catch (IOException ex) {
            Govu.logger.error("IOException", ex);
            res = ex.getMessage();
        }


        if (redirect != null) {
            response = new DefaultHttpResponse(HTTP_1_1, FOUND);
            response.setHeader("Location", redirect);
        } else {
            response = new DefaultHttpResponse(HTTP_1_1, OK);
            response.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");
            response.setContent(ChannelBuffers.copiedBuffer(res, CharsetUtil.UTF_8));
        }

        if (renderer != null) {
            for (Iterator<HttpCookie> it = renderer.getCookieEncoder().iterator(); it.hasNext();) {
                HttpCookie httpCookie = it.next();
                CookieEncoder encoder = new CookieEncoder(false);
                encoder.addCookie(httpCookie.getName(), httpCookie.getValue());

                response.setHeader("Set-Cookie", httpCookie.toString());
            }
        }

        future = e.getChannel().write(response);
        future.addListener(ChannelFutureListener.CLOSE);
    }
    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;

    public void writeFile(File file, ChannelHandlerContext ctx, HttpRequest request, MessageEvent e) {
        try {
            // Cache Validation
            String ifModifiedSince = request.getHeader(IF_MODIFIED_SINCE);
            if (ifModifiedSince != null && ifModifiedSince.length() != 0) {
                try {
                    SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
                    Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);

                    // Only compare up to the second because the datetime format we send to the client does
                    // not have milliseconds
                    long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
                    long fileLastModifiedSeconds = file.lastModified() / 1000;
                    if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                        sendNotModified(ctx);
                        return;
                    }
                } catch (ParseException ex) {
                    java.util.logging.Logger.getLogger(HttpServerHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            RandomAccessFile raf;
            try {
                raf = new RandomAccessFile(file, "r");
            } catch (FileNotFoundException fnfe) {
                sendError(ctx, NOT_FOUND,"f");
                return;
            }
            long fileLength = raf.length();

            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
            response.setHeader(CONTENT_LENGTH, fileLength);
            //setContentTypeHeader(response, file);
            setDateAndCacheHeaders(response, file);

            Channel ch = e.getChannel();

            // Write the initial line and the header.
            ch.write(response);

            // Write the content.
            ChannelFuture writeFuture;

            // No encryption - use zero-copy.
            final FileRegion region = new DefaultFileRegion(raf.getChannel(), 0, fileLength);
            writeFuture = ch.write(region);
            writeFuture.addListener(new ChannelFutureProgressListener() {
                public void operationComplete(ChannelFuture future) {
                    region.releaseExternalResources();
                }

                public void operationProgressed(
                        ChannelFuture future, long amount, long current, long total) {
                }
            });


            writeFuture.addListener(ChannelFutureListener.CLOSE);

        } catch (IOException ex) {
            logger.error(ex);
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.error(e.getCause());
    }

    private static String sanitizeUri(String uri) {
        // Decode the path.
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            try {
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            } catch (UnsupportedEncodingException e1) {
                throw new Error();
            }
        }

        // Convert file separators.
        uri = uri.replace('/', File.separatorChar);

        // Simplistic dumb security check.
        // You will have to do something serious in the production environment.
        if (uri.contains(File.separator + '.')
                || uri.contains('.' + File.separator)
                || uri.startsWith(".") || uri.endsWith(".")) {
            return null;
        }

        // Convert to absolute path.
        return System.getProperty("user.dir") + File.separator + uri;
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status,String code) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(
                "Failure: " + status.toString() + "."+ code+ "\r\n",
                CharsetUtil.UTF_8));

        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * When file timestamp is the same as what the browser is sending up, send a
     * "304 Not Modified"
     *
     * @param ctx Context
     */
    private static void sendNotModified(ChannelHandlerContext ctx) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, NOT_MODIFIED);
        setDateHeader(response);

        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * Sets the Date header for the HTTP response
     *
     * @param response HTTP response
     */
    private static void setDateHeader(HttpResponse response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.setHeader(DATE, dateFormatter.format(time.getTime()));
    }

    /**
     * Sets the Date and Cache headers for the HTTP Response
     *
     * @param response HTTP response
     * @param fileToCache file to extract content type
     */
    private static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.setHeader(DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.setHeader(EXPIRES, dateFormatter.format(time.getTime()));
        response.setHeader(CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.setHeader(LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }

    /**
     * Sets the content type header for the HTTP Response
     *
     * @param response HTTP response
     * @param file file to extract content type
     */
    private static void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.setHeader(CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
    }
}