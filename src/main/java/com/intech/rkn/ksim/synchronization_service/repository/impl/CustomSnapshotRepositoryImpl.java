package com.intech.rkn.ksim.synchronization_service.repository.impl;

import com.intech.rkn.ksim.synchronization_service.dto.MsisdnDataDto;
import com.intech.rkn.ksim.synchronization_service.repository.CustomSnapshotRepository;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyIn;
import org.postgresql.copy.CopyManager;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.thymeleaf.ITemplateEngine;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@Slf4j
@Repository
public class CustomSnapshotRepositoryImpl extends AbstractRepository implements CustomSnapshotRepository {

    private final static byte[] FIELD_SEPARATOR = "\t".getBytes();
    private final static byte[] ROW_SEPARATOR = "\n".getBytes();
    private final static String COPY_SQL = """
                  COPY sync.msisdn_data_snapshot(msisdn, parsed_at, operator_id, file_id, row_num, is_valid)
                  FROM STDIN WITH (FORMAT TEXT)
            """;

    private final static RowMapper<MsisdnDataDto> ROW_MAPPER = (rs, rowNum) -> MsisdnDataDto.builder()
            .msisdn(rs.getLong("msisdn"))
            .parsedAt(toLocalDateTime(rs.getTimestamp("parsed_at")))
            .operatorId(rs.getInt("operator_id"))
            .fileId(rs.getInt("file_id"))
            .rowNum(rs.getInt("row_num"))
            .isValid(rs.getBoolean("is_valid"))
            .build();

    private final JdbcTemplate jdbcTemplate;

    public CustomSnapshotRepositoryImpl(ITemplateEngine templateEngine,
                                        JdbcTemplate jdbcTemplate) {
        super(templateEngine);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void cleanUp() {
        jdbcTemplate.execute(getSql("clean-msisdn-data-snapshot-table-tpl"));
    }

    @Override
    public void makeSnapshot() {
        log.info("getting snapshot of msisdns data...");
        Stream<MsisdnDataDto> dataStream = jdbcTemplate.queryForStream(getSql("msisdn-data-snapshot-tpl"), ROW_MAPPER);
        log.info("copy msisdns data to snapshot table...");
        copyToTable(dataStream);
    }

    private void copyToTable(Stream<MsisdnDataDto> stream) {
        jdbcTemplate.execute((ConnectionCallback<Boolean>) connection -> {
            CopyManager copyManager = connection.unwrap(PGConnection.class).getCopyAPI();
            CopyIn copyIn = copyManager.copyIn(COPY_SQL);

            try {
                stream.forEach(dto -> writeOneRow(copyIn, dto));
            } finally {
                if (copyIn.isActive()) copyIn.endCopy();
            }

            return true;
        });
    }

    private void writeOneRow(CopyIn copyIn, MsisdnDataDto dto) {
        try {
            writeField(copyIn, String.valueOf(dto.msisdn()).getBytes(), false);
            writeField(copyIn, String.valueOf(dto.parsedAt()).getBytes(), false);
            writeField(copyIn, String.valueOf(dto.operatorId()).getBytes(), false);
            writeField(copyIn, String.valueOf(dto.fileId()).getBytes(), false);
            writeField(copyIn, String.valueOf(dto.rowNum()).getBytes(), false);
            writeField(copyIn, String.valueOf(dto.isValid()).getBytes(), true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeField(CopyIn copyIn, byte[] bytes, boolean isLastField) throws SQLException {
        copyIn.writeToCopy(bytes, 0, bytes.length);
        byte[] separator = isLastField ? ROW_SEPARATOR : FIELD_SEPARATOR;
        copyIn.writeToCopy(separator, 0, separator.length);
    }
}