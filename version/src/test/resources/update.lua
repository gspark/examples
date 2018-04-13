if redis.call('EXISTS', KEYS[1]) == 1 then
    local sku = redis.call('HGET', KEYS[1], ARGV[1]);
    local jsku = cjson.decode(sku);
    if jsku['stock'] == tonumber(ARGV[2]) then
        jsku['stock'] = tonumber(ARGV[3]);
        return redis.call('HSET', KEYS[1], ARGV[1], cjson.encode(jsku));
    else
        return -1;
    end
end
return -2;
