package org.acme.objectstorage.s3;

import java.io.File;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

@Getter
@NoArgsConstructor
public class MultiparteArquivo {

    @FormParam("arquivo")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private File arquivo;

    @FormParam("nomeArquivo")
    @PartType(MediaType.TEXT_PLAIN)
    private String nomeArquivo;

}
