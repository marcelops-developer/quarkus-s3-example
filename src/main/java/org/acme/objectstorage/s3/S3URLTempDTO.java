package org.acme.objectstorage.s3;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class S3URLTempDTO implements Serializable {

    private String urlAssinadaGET;
    private String urlAssinadaPUT;
    private String chaveObjeto;
    private String urlNaoAssinada;

}
