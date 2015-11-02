/*
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved. This software is licensed
 * under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1] [1]
 * http://www.gnu.org/licenses/gpl.txt
 */
package pl.bsb.b2b.h2o.helper;

import javax.activation.DataSource;
import java.io.InputStream;

/**
 * The <code>Payload</code> representing either a file or stream from
 * hermes.
 * 
  */
public interface IPayload extends DataSource {
	
	public void setPayload(InputStream inputStream, String contentType);
}