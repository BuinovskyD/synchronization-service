package com.intech.rkn.ksim.synchronization_service.repository.impl;

import com.intech.rkn.ksim.synchronization_service.model.MnpComparisonEntity;
import com.intech.rkn.ksim.synchronization_service.repository.CustomMnpComparisonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyIn;
import org.postgresql.copy.CopyManager;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomMnpComparisonRepositoryImpl implements CustomMnpComparisonRepository {

    private final static byte[] FIELD_SEPARATOR = "\t".getBytes();
    private final static byte[] ROW_SEPARATOR = "\n".getBytes();

    private final static String COPY_SQL = """
            COPY sync.mnp_full_comparison (msisdn, mnp_id, mnp_code, mnp_mnc, donor_id, donor_code, donor_mnc,
                                           range_holder_id, range_holder_code, mnp_port_date, process_type)
            FROM STDIN WITH (FORMAT TEXT, NULL 'NULL')
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadDataStream(Stream<MnpComparisonEntity> stream) {
        log.info("clean up mnp comparison table");
        jdbcTemplate.execute("truncate sync.mnp_full_comparison;");

        log.info("upload full mnp data");
        jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
            CopyManager copyManager = connection.unwrap(PGConnection.class).getCopyAPI();
            CopyIn copyIn = copyManager.copyIn(COPY_SQL);

            try {
                stream.forEach(record -> writeOneRecord(copyIn, record));
            } finally {
                if (copyIn != null) copyIn.endCopy();
            }
            return null;
        });

        log.info("upload full mnp data finished");
    }

    private void writeOneRecord(CopyIn copyIn, MnpComparisonEntity record) {
        try {
            writeField(copyIn, String.valueOf(record.msisdn()).getBytes(), false);
            writeField(copyIn, getNullable(record.mnpId()).getBytes(), false);
            writeField(copyIn, record.mnpCode().getBytes(), false);
            writeField(copyIn, String.valueOf(record.mnpMnc()).getBytes(), false);
            writeField(copyIn, getNullable(record.donorId()).getBytes(), false);
            writeField(copyIn, record.donorCode().getBytes(), false);
            writeField(copyIn, String.valueOf(record.donorMnc()).getBytes(), false);
            writeField(copyIn, getNullable(record.rangeHolderId()).getBytes(), false);
            writeField(copyIn, record.rangeHolderCode().getBytes(), false);
            writeField(copyIn, String.valueOf(record.mnpPortDate()).getBytes(), false);
            writeField(copyIn, record.processType().getBytes(), true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeField(CopyIn copyIn, byte[] bytes, boolean isLastField) throws SQLException {
        copyIn.writeToCopy(bytes, 0, bytes.length);
        byte[] separator = isLastField ? ROW_SEPARATOR : FIELD_SEPARATOR;
        copyIn.writeToCopy(separator, 0, separator.length);
    }

    private String getNullable(Integer val) {
        return Optional.ofNullable(val).map(String::valueOf).orElse("NULL");
    }
}
